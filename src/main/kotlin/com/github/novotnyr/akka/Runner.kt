package com.github.novotnyr.akka

import akka.actor.typed.ActorSystem
import akka.cluster.typed.ClusterSingleton
import akka.cluster.typed.SingletonActor


fun main() {
    val system = ActorSystem.create(Sensor(), "smarthome")
    system.tell(Sensor.MeasureTemperature)

    val clusterSingleton = ClusterSingleton.get(system)
    clusterSingleton.init(SingletonActor.of(Aggregator(), "Aggregator"))
}