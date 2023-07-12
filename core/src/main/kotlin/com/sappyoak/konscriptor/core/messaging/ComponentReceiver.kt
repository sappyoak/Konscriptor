package com.sappyoak.konscriptor.core.messaging

import net.kyori.adventure.text.Component
import kotlinx.uuid.UUID

typealias ComponentSink = (Component) -> Unit

val CONSOLE_ID = UUID(longArrayOf(0, 0))

class ComponentReceiver(
    private val id: UUID,
    private val sink: ComponentSink
) {
    val isConsole: Boolean get() = id == CONSOLE_ID

    fun receive(message: Component) {
        sink(message)
    }

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other !is ComponentReceiver) return false
        return id == other.id
    }
}
