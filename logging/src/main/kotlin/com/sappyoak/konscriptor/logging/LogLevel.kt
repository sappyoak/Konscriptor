package com.sappyoak.konscriptor.logging

enum class LogLevel(val level: Int) {
    All(Int.MIN_VALUE),
    Versbose(0),
    Debug(20),
    Info(40),
    Warn(80),
    Error(200),
    Off(Int.MAX_VALUE);
}

