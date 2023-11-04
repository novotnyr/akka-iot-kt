package com.github.novotnyr.akka

import akka.actor.typed.ActorRef
import akka.actor.typed.javadsl.*
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import org.slf4j.Logger

inline fun <reified T> ServiceKey(id: String): ServiceKey<T> {
    return ServiceKey.create(T::class.java, id)
}

fun <T> empty(): Receive<T> = ReceiveBuilder.create<T>().build()

fun <T> receive(ruleApplication: ReceiveRule<T>.() -> Unit): Receive<T> {
    val receiveBuilder = ReceiveBuilder.create<T>()
    val receiveRule = ReceiveRule(receiveBuilder)
    ruleApplication(receiveRule)
    return receiveBuilder.build()
}

class ReceiveRule<T>(val receiveBuilder: ReceiveBuilder<T>) {
    inline fun <reified M : T> on(crossinline messageHandler: (M) -> Unit) {
        receiveBuilder.onMessage(M::class.java) {
            messageHandler(it)
            Behaviors.same()
        }
    }
}

val <T> AbstractBehavior<T>.log: Logger
    get() = context.log

val <T> AbstractBehavior<T>.receptionist: ActorRef<Receptionist.Command>
    get() = context.system.receptionist()

val <T> AbstractBehavior<T>.self: ActorRef<T>
    get() = context.self


class MessageAdapterRule<T>(val context: ActorContext<T>) {
    inline operator fun <reified M> invoke(crossinline adaptMessage: (M) -> T): ActorRef<M> {
        return context.messageAdapter(M::class.java) {
            adaptMessage(it)
        }
    }
}

val <T> AbstractBehavior<T>.adaptMessage: MessageAdapterRule<T>
    get() {
        return MessageAdapterRule(this.context)
    }
