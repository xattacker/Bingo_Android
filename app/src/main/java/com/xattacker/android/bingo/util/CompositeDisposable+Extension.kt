package com.xattacker.android.bingo.util

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun CompositeDisposable.add(disposable: Disposable?)
{
    if (disposable != null)
    {
        this.add(disposable)
    }
}