package com.sappyoak.konscriptor.core.logging.logger

import java.util.concurrent.CopyOnWriteArraySet

import com.sappyoak.konscriptor.core.messaging.ComponentReceiver

interface ComponentReceiverHolder {
    val receivers: MutableSet<ComponentReceiver>

    fun addReceiver(receiver: ComponentReceiver) {
        receivers.add(receiver)
    }

    fun addReceivers(set: Set<ComponentReceiver>) {
        receivers.addAll(set)
    }

    fun removeReceiver(receiver: ComponentReceiver) {
        receivers.remove(receiver)
    }

    fun removeReceivers(set: Set<ComponentReceiver>) {
        receivers.removeAll(set)
    }

    fun clear() {
        receivers.clear()
    }
}

class PlatformSender : ComponentReceiverHolder {
    override val receivers: MutableSet<ComponentReceiver> = CopyOnWriteArraySet()

    fun send(event: LogEvent) {}
}