package com.badrun.my259firdaus.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.badrun.my259firdaus.model.Buku

class BookViewModel : ViewModel() {
    private val _books = MutableLiveData<List<Buku>>()
    val books: LiveData<List<Buku>> get() = _books

    fun setBooks(bookList: List<Buku>) {
        _books.value = bookList
    }
}