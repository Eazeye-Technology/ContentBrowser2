package com.txkj.contentbrowser;


public class LibraryFragment2Readings extends LibraryFragment2Book {
    @Override
    public int getInitIndex() {
        return 0; //0:all, 1:pdf, 2:epub(book)
    }
}