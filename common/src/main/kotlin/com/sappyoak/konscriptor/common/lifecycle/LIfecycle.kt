package com.sappyoak.konscriptor.common.lifecycle

import kotlinx.coroutines.CoroutineScope

/**
 * The underlying interface for an object that has a well defined lifetime within Konscriptor
 * and emits values that reflect it's current state in that time. Lifecycle is a way to coorindate
 * those actions and manage them in a scalable and hierarchical way in the way similar to how the Lifecycle
 * implementation on Android functions.
 *
 * All Lifecycles manage their own subscriptions throughout the hierarchy. A Lifecycle has a set of observers
 * listening for actions to occur, and a list of [LifecycleTag]'s representing any child lifecycles that have been
 * created under this one.
 *
 * An example of this hierarchy would be
 *      - Root Level:           [ApplicationLifecycle]:   This is the greatest parent lifecycle. All other lifecycles
 *                                                        are a child of this one
 *
 *      - Script Project Level: [ScriptProjectLifecycle]: A Lifecycle that is bound to a current project (collection of grouped files)
 *                                                        currently on disk. It responds and creates many new [LifecycleAction]'s that are
 *                                                        confined soley to it's level and any potential children created that don't leak into
 *                                                        the upper hierarchy.
 *
 *      - Dependency Level:     [DependencyTag]:          A Dependency Lifecycle is a bit different than the other two in that it can be a direct child of
 *                                                        the [ApplicationLifecycle] *ur* the [ScriptProjectLifecycle] lifecycle. Dependency lifecycles
 *                                                        are for hooking into third-party dependencies and either mapping into their internal Lifecycle state,
 *                                                         or by pushing changes to these dependencies on response to events from their ancestor hierarchy.
 *
 * All lifecycles are bound to a [CoroutineScope] which implements a similar designed to take advantage of the similar
 * hierarchical structure of structured concurrency so that Lifecycles are inherently self clearning. When the Application CoroutineScope
 * shuts down, all of it's children shut down as well. This applies for all Lifecycles in the hierarchy as well, so if A [ScriptProjectLifecycle]
 * has a few [DependencyLifecycle]'s the when the [ScriptProjectLifecycle] is shutdown, the child lifecycle's will be as well and so will
 * their associated CoroutineScopes ensuring that all jobs complete.
 */

interface Lifecycle {
    val tag: LifecycleTag
    val childTags: Set<LifecycleTag>
    val scope: CoroutineScope
    val acceptsParentActions: Boolean

    fun onReceive(action: LifecycleAction)

    fun getObservers(): List<LifecycleObserverHandle>
    fun addObserver(observer: LifecycleObserverHandle)
    fun removeObserver(observer: LifecycleObserverHandle)

    fun addChildTag(childTag: LifecycleTag)
    fun removeChildTag(childTag: LifecycleTag)
}

fun Lifecycle.addObserver(priority: Int = 1, observer: LifecycleObserver) {
    val weighted =
        if (observer is LifecycleObserverHandle) observer
        else LifecycleObserverHandle(priority, observer)
    addObserver(weighted)
}

inline fun <T : LifecycleObserver> Lifecycle.addObserver(priority: Int = 1, block: () -> T): T {
    return block().also { addObserver(priority, it) }
}

fun Lifecycle(tag: LifecycleTag, scope: CoroutineScope): Lifecycle {
    return BasicLifecycle(tag, scope)
}