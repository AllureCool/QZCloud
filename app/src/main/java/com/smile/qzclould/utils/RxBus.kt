package com.smile.qzclould.utils


import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import java.util.concurrent.ConcurrentHashMap

object RxBus {

    private val mBus: Relay<Any> = PublishRelay.create<Any>().toSerialized()

    private val mStickyEvents: MutableMap<Class<*>, Any> = ConcurrentHashMap()


    fun post(o: Any) {
        mBus.accept(o)
    }

    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return mBus.ofType(eventType)
    }

    fun postSticky(event: Any) {
        mStickyEvents.put(event.javaClass, event)
        post(event)
    }

    fun <T> toObservableSticky(eventType: Class<T>): Observable<T> {
        val observable = mBus.ofType(eventType)
        val event = mStickyEvents[eventType]

        return if (event != null) {
            observable.mergeWith(Observable.create { observableEmitter -> observableEmitter.onNext(eventType.cast(event)) })
        } else {
            observable
        }
    }

    fun clearSticky() {
        mStickyEvents.clear()
    }

    fun clearSticky(clazz: Class<*>) {
        mStickyEvents.remove(clazz)
    }
}
