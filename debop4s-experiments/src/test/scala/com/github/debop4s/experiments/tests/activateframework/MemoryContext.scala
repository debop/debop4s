package com.github.debop4s.experiments.tests.activateframework

import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.storage.memory.TransientMemoryStorage

/**
 * MemoryContext
 * @author Sunghyouk Bae
 */
object MemoryContext extends ActivateContext {

    override val storage = new TransientMemoryStorage

}
