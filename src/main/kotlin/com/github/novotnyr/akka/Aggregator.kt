package com.github.novotnyr.akka

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Behaviors.setup
import akka.actor.typed.javadsl.Receive
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey


val AGGREGATOR = ServiceKey<Aggregator.Command>("Aggregator")

class Aggregator(context: ActorContext<Command>) : AbstractBehavior<Aggregator.Command>(context) {
    override fun createReceive(): Receive<Command> = empty()

    sealed class Command
}

fun Aggregator() = setup { ctx ->
    Aggregator(ctx).apply {
        receptionist.tell(Receptionist.register(AGGREGATOR, self))
        log.info("Registered itself as an aggregator")
    }
}