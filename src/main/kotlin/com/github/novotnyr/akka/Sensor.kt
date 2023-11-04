package com.github.novotnyr.akka

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors.setup
import akka.actor.typed.javadsl.Behaviors.withTimers
import akka.actor.typed.receptionist.Receptionist
import java.time.Duration


class Sensor(context: ActorContext<Command>) : AbstractBehavior<Sensor.Command>(context) {
    private var aggregator: ActorRef<Aggregator.Command> = context.system.deadLetters()

    private val receptionistMessageAdapter = adaptMessage { listing: Receptionist.Listing ->
        val aggregators = listing.getServiceInstances(AGGREGATOR)
        when (aggregators.size) {
            1 -> aggregators.first().also {
                context.log.info("Receiving an aggregator instance")
            }

            else -> {
                context.log.warn("Incorrect number of Aggregators. Found: {}", aggregators.size)
                context.system.deadLetters()
            }
        }.let { SetAggregator(it) }
    }

    init {
        receptionist.tell(Receptionist.subscribe(AGGREGATOR, receptionistMessageAdapter))
    }

    override fun createReceive() = receive {
        on<MeasureTemperature> {
            log.debug("Will measure temperature")
        }
        on<SetAggregator> {
            aggregator = it.aggregator
        }
    }

    sealed class Command

    data object MeasureTemperature : Command()

    data class SetAggregator(val aggregator: ActorRef<Aggregator.Command>) : Command()
}

fun Sensor(): Behavior<Sensor.Command> {
    return setup { ctx ->
        withTimers {
            it.startTimerWithFixedDelay(Sensor.MeasureTemperature, Duration.ofSeconds(2))
            Sensor(ctx)
        }
    }
}

