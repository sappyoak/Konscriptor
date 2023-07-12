package com.sappyoak.konscriptor.core.utils

import kotlin.random.Random
import kotlin.random.nextUInt

fun randomId(): String = Random.nextUInt().toString(16)