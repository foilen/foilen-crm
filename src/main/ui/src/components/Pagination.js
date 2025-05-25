import React from 'react'
import './Pagination.css'

function Pagination(
    {
        pagination = {
            currentPageUi: 1,
            totalPages: 1,
            firstPage: true,
            lastPage: true
        },
        onChangePage,
        className
    }) {
    const previous = () => {
        onChangePage({
            pageId: pagination.currentPageUi - 1
        })
    }

    const next = () => {
        onChangePage({
            pageId: pagination.currentPageUi + 1
        })
    }

    return (
        <div className={`mb-3 ${className || ''}`}>
            <button
                className="btn btn-small btn-primary"
                disabled={pagination.firstPage}
                onClick={previous}
            >
                &lt;
            </button>
            {pagination.currentPageUi} / {pagination.totalPages}
            <button
                className="btn btn-small btn-primary"
                disabled={pagination.lastPage}
                onClick={next}
            >
                &gt;
            </button>
        </div>
    )
}

export default Pagination
