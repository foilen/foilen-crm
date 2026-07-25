import React from 'react'
import './PaginationControl.css'

function PaginationControl(
    {
        state = {
            currentPageUi: 1,
            totalPages: 1,
            firstPage: true,
            lastPage: true
        },
        onPageChange,
        className
    }) {
    return (
        <div className={`mb-3 ${className || ''}`}>
            <button
                className="btn btn-small btn-primary"
                disabled={state.firstPage}
                onClick={() => onPageChange(state.currentPageUi - 1)}
            >
                &lt;
            </button>
            {state.currentPageUi} / {state.totalPages}
            <button
                className="btn btn-small btn-primary"
                disabled={state.lastPage}
                onClick={() => onPageChange(state.currentPageUi + 1)}
            >
                &gt;
            </button>
        </div>
    )
}

export default PaginationControl
