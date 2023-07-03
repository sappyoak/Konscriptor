package com.sappyoak.konscriptor.dsl.platform.extensions

import org.bukkit.BlockChangeDelegate
import org.bukkit.Chunk
import org.bukkit.Effect
import org.bukkit.HeightMap
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.TreeType
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.LivingEntity
import org.bukkit.generator.structure.Structure
import org.bukkit.generator.structure.StructureType
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import kotlin.reflect.KClass

typealias NullableConsumer<T> = ((T) -> Unit)?
typealias NullablePredicate<T> = ((T) -> Boolean)?

operator fun Location.component1(): Double = x
operator fun Location.component2(): Double = y
operator fun Location.component3(): Double = z
operator fun Location.component4(): Float = yaw
operator fun Location.component5(): Float = pitch


val Location.atBlock: Block get() = world.getBlockAt(this)
val Location.highestBlock: Block get() = world.getHighestBlockAt(this)
fun Location.highestBlock(heightMap: HeightMap) = world.getHighestBlockAt(this, heightMap)

val Location.pluginChunkTickets: MutableCollection<Plugin> get() = world.pluginChunkTickets(this)

val Location.humidity: Double get() = world.humidity(this)
val Location.temperature: Double get() = world.temperature(this)


val Location.isChunkForceLoaded: Boolean get() = world.isChunkForceLoaded(this)
val Location.isChunkGenerated: Boolean get() = world.isChunkGenerated(this)
fun Location.asyncChunkAt(generate: Boolean = false) = world.getChunkAtAsync(this, generate)
fun Location.asyncChunkAt(generate: Boolean, consumer: (Chunk) -> Unit) = world.getChunkAtAsync(this, generate, consumer)
fun Location.urgentAsyncChunkAt(generate: Boolean = false) = world.getChunkAtAsyncUrgently(this, generate)
fun Location.loadChunk(generate: Boolean = false) = world.loadChunk(this, generate)
fun Location.refreshChunk() = world.refreshChunk(this)
fun Location.setForceLoaded(forced: Boolean) = world.setForceLoaded(this, forced)
fun Location.unloadChunk(save: Boolean) = world.unloadChunk(this, save)
fun Location.unloadChunkRequest() = world.unloadChunkRequest(this)

fun Location.nearestBiome(biome: Biome, radius: Int) = world.locateNearestBiome(this, biome, radius)
fun Location.nearestBiome(biome: Biome, radius: Int, step: Int) = world.locateNearestBiome(this, biome, radius, step)
fun Location.nearestRaid(radius: Int) = world.locateNearestRaid(this, radius)
fun Location.nearestStructure(structure: Structure, radius: Int, findUnexplored: Boolean = false) = world.locateNearestStructure(this, structure, radius, findUnexplored)
fun Location.nearestStructure(type: StructureType, radius: Int, findUnexplored: Boolean = false) = world.locateNearestStructure(this, type, radius, findUnexplored)


fun Location.effect(effect: Effect, data: Int) = world.playEffect(this, effect, data)
fun Location.effect(effect: Effect, data: Int, radius: Int) = world.playEffect(this, effect, data, radius)
fun <T> Location.effect(effect: Effect, data: T) = world.playEffect(this, effect, data)
fun <T> Location.effect(effect: Effect, data: T, radius: Int) = world.playEffect(this, effect, data, radius)
fun Location.lightningStrike(): LightningStrike = world.strikeLightning(this)
fun Location.lightningEffect(): LightningStrike = world.strikeLightningEffect(this)
fun Location.sound(sound: Sound, volume: Float, pitch: Float) = world.playSound(this, sound, volume, pitch)

inline fun <reified T : Entity> Location.spawn(): T = world.spawn(this, T::class.java)
fun Location.spawnArrow(direction: Vector, speed: Float, spread: Float) = world.spawnArrow(this, direction, speed, spread)
inline fun <reified T : AbstractArrow> Location.spawnArrow(direction: Vector, speed: Float, spread: Float): T = world.spawnArrow(this, direction, speed, spread, T::class.java)
fun Location.spawnFallingBlock(data: BlockData) = world.spawnFallingBlock(this, data)

fun Location.spawnParticle(particle: Particle, count: Int) = world.spawnParticle(particle, this, count)
fun Location.spawnParticle(particle: Particle, count: Int, offset: Location) = world.spawnParticle(particle, this, count, offset)

fun Location.explode(power: Float): Boolean = world!!.createExplosion(this, power)
fun Location.explode(power: Float, setFire: Boolean): Boolean = world.createExplosion(this, power, setFire)
fun Location.dropItem(item: ItemStack): Item = world.dropItem(this, item)
fun Location.dropItem(item: ItemStack, consumer: NullableConsumer<Item>): Item = world.dropItem(this, item, consumer)
fun Location.dropItemNaturally(item: ItemStack): Item = world.dropItemNaturally(this, item)
fun Location.dropItemNaturally(item: ItemStack, consumer: NullableConsumer<Item>): Item = world.dropItemNaturally(this, item, consumer)
fun Location.findLightningRod(): Location? = world.findLightningRod(this)
fun Location.findLightningTarget(): Location? = world.findLightningTarget(this)
fun Location.generateTree(type: TreeType): Boolean = world.generateTree(this, type)

fun Location.nearbyEntities(other: Location, predicate: NullablePredicate<Entity>) = world.nearbyEntities(this, other, predicate)

fun <T : Entity> Location.nearbyEntitiesOfType(
    type: KClass<T>,
    xRadius: Double,
    yRadius: Double? = null,
    zRadius: Double? = null,
    predicate: NullablePredicate<T> = null
): MutableCollection<T> = world.nearbyEntitiesOfType(type, this, xRadius, yRadius, zRadius, predicate)

inline fun <reified T : Entity> Location.nearbyEntitiesOfType(
    xRadius: Double,
    yRadius: Double? = null,
    zRadius: Double? = null,
    noinline predicate: NullablePredicate<T> = null
): MutableCollection<T> = world.nearbyEntitiesOfType(T::class, this, xRadius, yRadius, zRadius, predicate)

fun Location.nearbyLivingEntities(
    xRadius: Double,
    yRadius: Double? = null,
    zRadius: Double? = null,
    predicate: NullablePredicate<LivingEntity> = null
): MutableCollection<LivingEntity> = world.nearbyLivingEntities(this, xRadius, yRadius, zRadius, predicate)

fun Location.nearbyPlayers(
    xRadius: Double,
    yRadius: Double? = null,
    zRadius: Double? = null,
    predicate: NullablePredicate<Player> = null
): MutableCollection<Player> = world.nearbyPlayers(this, xRadius, yRadius, zRadius, predicate)
