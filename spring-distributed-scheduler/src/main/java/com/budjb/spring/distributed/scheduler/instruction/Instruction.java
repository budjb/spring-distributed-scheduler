package com.budjb.spring.distributed.scheduler.instruction;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Defines an instruction based on the {@link Callable} interface, and requires
 * the class to be serializable.
 *
 * @param <T> Type that the callable will return.
 */
public interface Instruction<T> extends Callable<T>, Serializable {

}
