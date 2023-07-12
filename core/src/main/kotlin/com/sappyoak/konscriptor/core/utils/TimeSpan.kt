package com.sappyoak.konscriptor.core.utils

import kotlin.time.*

import com.sappyoak.konscriptor.core.Constants

typealias TimeSpan = kotlin.time.Duration

fun TimeSpan(ms: Double): TimeSpan = ms.toDuration(DurationUnit.MILLISECONDS)
fun TimeSpan(ms: Int): TimeSpan = ms.toDuration(DurationUnit.MILLISECONDS)

val Duration.Companion.ZERO get() = TimeSpan(0.0)
val Duration.Companion.NIL get() = (Long.MAX_VALUE / 2 - 1).toDuration(DurationUnit.MILLISECONDS)

fun Duration.Companion.fromMilliseconds(ms: Double) = when (ms) {
    0.0 -> ZERO
    else -> TimeSpan(ms)
}
fun Duration.Companion.fromMilliseconds(ms: Int) = when (ms) {
    0 -> ZERO
    else -> TimeSpan(ms)
}

fun Duration.Companion.fromNanoseconds(s: Double) = fromMilliseconds(s * Constants.MS_PER_NANOSECOND)
fun Duration.Companion.fromMicroseconds(s: Double) = fromMilliseconds(s * Constants.MS_PER_MICROSECOND)
fun Duration.Companion.fromSeconds(s: Double) = fromMilliseconds(s * Constants.MS_PER_SECOND)
fun Duration.Companion.fromMinutes(s: Double) = fromMicroseconds(s * Constants.MS_PER_MINUTE)
fun Duration.Companion.fromHours(s: Double) = fromMilliseconds(s * Constants.MS_PER_HOUR)
fun Duration.Companion.fromDays(s: Double) = fromMicroseconds(s * Constants.MS_PER_DAY)
fun Duration.Companion.fromWeeks(s: Double) = fromMicroseconds(s * Constants.MS_PER_WEEK)

inline val Int.nanoseconds get() = Duration.fromNanoseconds(this.toDouble())
inline val Int.microseconds get() = Duration.fromMicroseconds(this.toDouble())
inline val Int.milliseconds get() = Duration.fromMilliseconds(this.toDouble())
inline val Int.seconds get() = Duration.fromSeconds(this.toDouble())
inline val Int.minutes get() = Duration.fromMinutes(this.toDouble())
inline val Int.hours get() = Duration.fromHours(this.toDouble())
inline val Int.days get() = Duration.fromDays(this.toDouble())
inline val Int.weeks get() = Duration.fromDays(this.toDouble())

inline val Long.nanoseconds get() = Duration.fromNanoseconds(this.toDouble())
inline val Long.microseconds get() = Duration.fromMicroseconds(this.toDouble())
inline val Long.milliseconds get() = Duration.fromMilliseconds(this.toDouble())
inline val Long.seconds get() = Duration.fromSeconds(this.toDouble())
inline val Long.minutes get() = Duration.fromMinutes(this.toDouble())
inline val Long.hours get() = Duration.fromHours(this.toDouble())
inline val Long.days get() = Duration.fromDays(this.toDouble())
inline val Long.weeks get() = Duration.fromWeeks(this.toDouble())

inline val Double.nanoseconds get() = Duration.fromMilliseconds(this)
inline val Double.microseconds get() = Duration.fromMicroseconds(this)
inline val Double.milliseconds get() = Duration.fromMilliseconds(this)
inline val Double.seconds get() = Duration.fromSeconds(this)
inline val Double.minutes get() = Duration.fromMinutes(this)
inline val Double.hours get() = Duration.fromHours(this)
inline val Double.days get() = Duration.fromDays(this)
inline val Double.weeks get() = Duration.fromWeeks(this)

inline val Float.nanoseconds get() = Duration.fromMilliseconds(this.toDouble())
inline val Float.microseconds get() = Duration.fromMicroseconds(this.toDouble())
inline val Float.milliseconds get() = Duration.fromMilliseconds(this.toDouble())
inline val Float.seconds get() = Duration.fromSeconds(this.toDouble())
inline val Float.minutes get() = Duration.fromMinutes(this.toDouble())
inline val Float.hours get() = Duration.fromHours(this.toDouble())
inline val Float.days get() = Duration.fromDays(this.toDouble())
inline val Float.weeks get() = Duration.fromWeeks(this.toDouble())

val Duration.nanoseconds: Double get() = milliseconds / Constants.MS_PER_NANOSECOND
val Duration.nanosecondsInt: Int get() = nanoseconds.toInt()
val Duration.nanosecondsLong: Long get() = nanoseconds.toLong()

val Duration.microseconds: Double get() = milliseconds / Constants.MS_PER_MICROSECOND
val Duration.microsecondsInt: Int get() = microseconds.toInt()
val Duration.microsecondsLong: Long get() = microseconds.toLong()

val Duration.milliseconds: Double get() = toDouble(DurationUnit.MILLISECONDS)
val Duration.millisecondsInt: Int get() = milliseconds.toInt()
val Duration.millisecondsLong: Long get() = milliseconds.toLong()

val Duration.seconds: Double get() = milliseconds / Constants.MS_PER_SECOND
val Duration.secondsInt: Int get() = seconds.toInt()
val Duration.secondsLong: Long get() = seconds.toLong()

val Duration.minutes: Double get() = milliseconds / Constants.MS_PER_MINUTE
val Duration.minutesInt: Int get() = minutes.toInt()
val Duration.minutesLong: Long get() = minutes.toLong()

val Duration.hours: Double get() = milliseconds / Constants.MS_PER_HOUR
val Duration.hoursInt: Int get() = hours.toInt()
val Duration.hoursLong: Long get() = hours.toLong()

val Duration.days: Double get() = milliseconds / Constants.MS_PER_DAY
val Duration.daysInt: Int get() = days.toInt()
val Duration.daysLong: Long get() = days.toLong()

val Duration.weeks: Double get() = milliseconds / Constants.MS_PER_WEEK
val Duration.weeksInt: Int get() = weeks.toInt()
val Duration.weeksLong: Long get() = weeks.toLong()


val Duration.isNil: Boolean get() = this == Duration.NIL
val Duration.isZero: Boolean get() = this == Duration.ZERO

operator fun Duration.unaryMinus() = TimeSpan(-milliseconds)
operator fun Duration.unaryPlus() = TimeSpan(+milliseconds)

operator fun Duration.plus(other: TimeSpan): TimeSpan = TimeSpan(milliseconds + other.milliseconds)
operator fun Duration.minus(other: TimeSpan): TimeSpan = this + (-other)

operator fun Duration.times(scale: Int): TimeSpan = TimeSpan(milliseconds * scale)
operator fun Duration.times(scale: Float): TimeSpan = TimeSpan(milliseconds * scale)
operator fun Duration.times(scale: Double): TimeSpan = TimeSpan(milliseconds * scale)

operator fun Duration.div(scale: Int): TimeSpan = TimeSpan(milliseconds / scale)
operator fun Duration.div(scale: Float): TimeSpan = TimeSpan(milliseconds / scale)
operator fun Duration.div(scale: Double): TimeSpan = TimeSpan(milliseconds / scale)
operator fun Duration.div(other: TimeSpan): Double = (milliseconds / other.milliseconds)
// Ticks and stuff

val Duration.ticks: Double get() = millisToTick(toDouble(DurationUnit.MILLISECONDS))
val Duration.ticksLong: Long get() = ticks.toLong()

inline val Double.tickMs get() = Duration.fromMilliseconds(tickToMillis(this)).milliseconds
inline val Long.ticksMs get() = toDouble().tickMs
inline val Int.ticksMs get() = toDouble().tickMs

inline val Double.ticks get() = Duration.fromMilliseconds(millisToTick(this)).milliseconds
inline val Long.ticks get() = toDouble().ticks
inline val Int.ticks get() = toDouble().ticks

fun tickToMillis(value: Double): Double = value * Constants.MS_PER_SERVER_TICK
fun millisToTick(value: Double): Double = value / Constants.MS_PER_SERVER_TICK