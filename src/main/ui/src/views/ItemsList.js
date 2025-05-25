import React, {useEffect, useState} from 'react'
import ErrorResults from '../components/ErrorResults'
import Pagination from '../components/Pagination'
import ClientSelect from '../components/ClientSelect'
import {delete as del, get, post, put, showSuccess} from '../utils/http'
import {dateNowDayOnly, priceToLong} from '../utils/features'
import {t} from '../utils/TranslationUtils'

function ItemsList() {
    // State for billed items
    const [billed, setBilled] = useState({
        queries: {},
        items: [],
        pagination: {
            currentPageUi: 1,
            totalPages: 1,
            firstPage: true,
            lastPage: true,
        },
    })

    // State for pending items
    const [pending, setPending] = useState({
        queries: {},
        items: [],
        pagination: {
            currentPageUi: 1,
            totalPages: 1,
            firstPage: true,
            lastPage: true,
        },
    })

    // Form states
    const [billForm, setBillForm] = useState({})
    const [createForm, setCreateForm] = useState({
        date: dateNowDayOnly(),
    })
    const [editForm, setEditForm] = useState({
        clientShortName: null,
    })
    const [formResult, setFormResult] = useState({})

    // Function to refresh both billed and pending items
    const refresh = (pageId) => {
        if (pageId === undefined) {
            pageId = 1
        }

        refreshBilled(pageId)
        refreshPending(pageId)
    }

    // Function to refresh billed items
    const refreshBilled = async (pageId) => {
        if (pageId === undefined) {
            pageId = 1
        }

        setBilled(prev => ({
            ...prev,
            queries: {
                ...prev.queries,
                pageId
            }
        }))

        console.log('Items - Load Billed', {pageId})

        try {
            const response = await get('/api/item/listBilled', {pageId})
            setBilled(prev => ({
                ...prev,
                pagination: response.data.pagination,
                items: response.data.items || []
            }))
        } catch (error) {
            console.error('Error loading billed items', error)
            setBilled(prev => ({
                ...prev,
                items: []
            }))
        }
    }

    // Function to refresh pending items
    const refreshPending = async (pageId) => {
        if (pageId === undefined) {
            pageId = 1
        }

        setPending(prev => ({
            ...prev,
            queries: {
                ...prev.queries,
                pageId
            }
        }))

        console.log('Items - Load Pending', {pageId})

        try {
            const response = await get('/api/item/listPending', {pageId})
            setPending(prev => ({
                ...prev,
                pagination: response.data.pagination,
                items: response.data.items || []
            }))
        } catch (error) {
            console.error('Error loading pending items', error)
            setPending(prev => ({
                ...prev,
                items: []
            }))
        }
    }

    // Function to show create modal
    const showCreate = () => {
        setFormResult({})
    }

    // Function to create a new item
    const create = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(createForm))
        clonedForm.price = priceToLong(clonedForm.price)
        console.log('Item - Create', clonedForm)

        try {
            const response = await post('/api/item', clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#createModal .btn-secondary').click()
                showSuccess(t('prompt.create.success', {0: createForm.description}))
                refreshPending(pending.queries.pageId)
            }
        } catch (error) {
            console.error('Error creating item', error)
        }
    }

    // Function to create an item with time
    const createWithTime = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(createForm))
        console.log('Item - Create With Time', clonedForm)

        try {
            const response = await post('/api/item/createWithTime', clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#createWithTimeModal .btn-secondary').click()
                refreshPending(pending.queries.pageId)
            }
        } catch (error) {
            console.error('Error creating item with time', error)
        }
    }

    // Function to bill all pending items
    const billPending = async () => {
        setFormResult({})
        console.log('Item - Bill Pending', billForm)

        try {
            const response = await post('/api/item/billPending', billForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#billPendingModal .btn-secondary').click()
                refresh()
            }
        } catch (error) {
            console.error('Error billing pending items', error)
        }
    }

    // Function to bill selected items
    const billSelected = async () => {
        setFormResult({})
        console.log('Item - Bill Selected', billForm)

        try {
            const response = await post('/api/item/billSomePending', billForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#billSelectedModal .btn-secondary').click()
                refresh()
            }
        } catch (error) {
            console.error('Error billing selected items', error)
        }
    }

    // Function to delete an item
    const deleteOne = async (item) => {
        if (window.confirm(t('prompt.delete.confirm', {0: item.id}))) {
            console.log('Item - Pending - Delete', item.id)

            try {
                await del('/api/item/' + item.id)
                showSuccess(t('prompt.delete.success', {0: item.id}))
                refresh()
            } catch (error) {
                console.error('Error deleting item', error)
            }
        }
    }

    // Function to show bill selected modal
    const showBillSelected = () => {
        setFormResult({})
        const itemToBillIds = []
        pending.items.forEach(element => {
            if (element.selected) {
                itemToBillIds.push(element.id)
            }
        })

        setBillForm({
            itemToBillIds
        })

        console.log(itemToBillIds)

        // Use Bootstrap's modal API to show the modal
        const modal = document.querySelector('#billSelectedModal')
        if (modal) {
            const bsModal = new window.bootstrap.Modal(modal)
            bsModal.show()
        }
    }

    // Function to show edit modal
    const showEdit = (item) => {
        setFormResult({})
        setEditForm({
            ...item,
            id: item.id,
            price: item.priceFormatted,
            date: item.dateFormatted,
            clientShortName: item.client ? item.client.shortName : null
        })
    }

    // Function to edit an item
    const edit = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(editForm))
        clonedForm.price = priceToLong(clonedForm.price)
        console.log('Item - Pending - Edit', clonedForm)

        try {
            const response = await put('/api/item/' + editForm.id, clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#editModal .btn-secondary').click()
                showSuccess(t('prompt.edit.success', {0: editForm.id}))
                refresh()
            }
        } catch (error) {
            console.error('Error editing item', error)
        }
    }

    // Function to handle checkbox change for pending items
    const handleCheckboxChange = (index) => {
        setPending(prev => {
            const newItems = [...prev.items]
            newItems[index] = {
                ...newItems[index],
                selected: !newItems[index].selected
            }
            return {
                ...prev,
                items: newItems
            }
        })
    }

    // Function to handle create form field changes
    const handleCreateFormChange = (field, value) => {
        setCreateForm(prev => ({
            ...prev,
            [field]: value
        }))
    }

    // Function to handle edit form field changes
    const handleEditFormChange = (field, value) => {
        setEditForm(prev => ({
            ...prev,
            [field]: value
        }))
    }

    // Function to handle bill form field changes
    const handleBillFormChange = (field, value) => {
        setBillForm(prev => ({
            ...prev,
            [field]: value
        }))
    }

    // Initial load
    useEffect(() => {
        refresh()
    }, [])

    return (
        <div className="row">
            <div className="col-12">
                <h1>{t('term.pending')}</h1>

                <button type="button" className="btn btn-success" data-bs-toggle="modal" onClick={showCreate}
                        data-bs-target="#createModal">{t('button.create')}</button>

                <div className="modal fade" id="createModal" tabIndex="-1" role="dialog"
                     aria-labelledby="createModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="createModalLabel">{t('button.create')}</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <ErrorResults formResult={formResult}/>

                                <div className="mb-3">
                                    <label htmlFor="clientShortName">{t('term.clientShortName')}</label>
                                    <ClientSelect
                                        id="clientShortName"
                                        value={createForm.clientShortName || ''}
                                        onChange={(value) => handleCreateFormChange('clientShortName', value)}
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.clientShortName.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="date">{t('term.date')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="date"
                                        value={createForm.date || ''}
                                        onChange={(e) => handleCreateFormChange('date', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.date && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.date.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="description">{t('term.description')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="description"
                                        value={createForm.description || ''}
                                        onChange={(e) => handleCreateFormChange('description', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.description && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.description.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="price">{t('term.price')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="price"
                                        value={createForm.price || ''}
                                        onChange={(e) => handleCreateFormChange('price', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.price && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.price.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="category">{t('term.category')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="category"
                                        value={createForm.category || ''}
                                        onChange={(e) => handleCreateFormChange('category', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.category && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.category.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary"
                                        data-bs-dismiss="modal">{t('button.close')}</button>
                                <button type="button" className="btn btn-success"
                                        onClick={create}>{t('button.create')}</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="editModal" tabIndex="-1" role="dialog" aria-labelledby="editModalLabel"
                     aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="editModalLabel">Edit</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <ErrorResults formResult={formResult}/>

                                <div className="mb-3">
                                    <label htmlFor="clientShortName2">Client Short Name</label>
                                    <ClientSelect
                                        id="clientShortName2"
                                        value={editForm.clientShortName || ''}
                                        onChange={(value) => handleEditFormChange('clientShortName', value)}
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.clientShortName.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="date2">Date</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="date2"
                                        value={editForm.date || ''}
                                        onChange={(e) => handleEditFormChange('date', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.date && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.date.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="description2">Description</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="description2"
                                        value={editForm.description || ''}
                                        onChange={(e) => handleEditFormChange('description', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.description && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.description.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="price2">Price</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="price2"
                                        value={editForm.price || ''}
                                        onChange={(e) => handleEditFormChange('price', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.price && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.price.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="category2">Category</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="category2"
                                        value={editForm.category || ''}
                                        onChange={(e) => handleEditFormChange('category', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.category && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.category.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary"
                                        data-bs-dismiss="modal">{t('button.close')}</button>
                                <button type="button" className="btn btn-success"
                                        onClick={edit}>{t('button.edit')}</button>
                            </div>
                        </div>
                    </div>
                </div>

                <button type="button" className="btn btn-success" data-bs-toggle="modal"
                        data-bs-target="#createWithTimeModal">{t('button.createWithTime')}</button>

                <div className="modal fade" id="createWithTimeModal" tabIndex="-1" role="dialog"
                     aria-labelledby="createWithTimeModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title"
                                    id="createWithTimeModalLabel">{t('button.createWithTime')}</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <ErrorResults formResult={formResult}/>

                                <div className="mb-3">
                                    <label htmlFor="clientShortName3">{t('term.clientShortName')}</label>
                                    <ClientSelect
                                        id="clientShortName3"
                                        value={createForm.clientShortName || ''}
                                        onChange={(value) => handleCreateFormChange('clientShortName', value)}
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.clientShortName.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="date3">{t('term.date')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="date3"
                                        value={createForm.date || ''}
                                        onChange={(e) => handleCreateFormChange('date', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.date && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.date.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="description3">{t('term.description')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="description3"
                                        value={createForm.description || ''}
                                        onChange={(e) => handleCreateFormChange('description', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.description && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.description.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="hours3">{t('term.hours')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="hours3"
                                        value={createForm.hours || ''}
                                        onChange={(e) => handleCreateFormChange('hours', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.hours && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.hours.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="minutes3">{t('term.minutes')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="minutes3"
                                        value={createForm.minutes || ''}
                                        onChange={(e) => handleCreateFormChange('minutes', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.minutes && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.minutes.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="category3">{t('term.category')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="category3"
                                        value={createForm.category || ''}
                                        onChange={(e) => handleCreateFormChange('category', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.category && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.category.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary"
                                        data-bs-dismiss="modal">{t('button.close')}</button>
                                <button type="button" className="btn btn-success"
                                        onClick={createWithTime}>{t('button.createWithTime')}</button>
                            </div>
                        </div>
                    </div>
                </div>

                <button type="button" className="btn btn-success" data-bs-toggle="modal"
                        data-bs-target="#billPendingModal">{t('button.billPending')}</button>

                <div className="modal fade" id="billPendingModal" tabIndex="-1" role="dialog"
                     aria-labelledby="billPendingModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="billPendingModalLabel">{t('button.billPending')}</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <ErrorResults formResult={formResult}/>

                                <div className="mb-3">
                                    <label htmlFor="invoicePrefix">{t('term.invoicePrefix')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="invoicePrefix"
                                        value={billForm.invoicePrefix || ''}
                                        onChange={(e) => handleBillFormChange('invoicePrefix', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.invoicePrefix && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.invoicePrefix.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary"
                                        data-bs-dismiss="modal">{t('button.close')}</button>
                                <button type="button" className="btn btn-success"
                                        onClick={billPending}>{t('button.billPending')}</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="billSelectedModal" tabIndex="-1" role="dialog"
                     aria-labelledby="billSelectedModalLabel" aria-hidden="true">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="billSelectedModalLabel">{t('button.billSelected')}</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <ErrorResults formResult={formResult}/>

                                <div className="mb-3">
                                    <label htmlFor="invoicePrefix2">{t('term.invoicePrefix')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="invoicePrefix2"
                                        value={billForm.invoicePrefix || ''}
                                        onChange={(e) => handleBillFormChange('invoicePrefix', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.invoicePrefix && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.invoicePrefix.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <p>{(billForm.itemToBillIds && billForm.itemToBillIds.length) || 0} {t('term.items')}</p>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary"
                                        data-bs-dismiss="modal">{t('button.close')}</button>
                                <button type="button" className="btn btn-success"
                                        onClick={billSelected}>{t('button.billSelected')}</button>
                            </div>
                        </div>
                    </div>
                </div>

                <Pagination className="float-end" pagination={pending.pagination}
                            onChangePage={(event) => refreshPending(event.pageId)}/>

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th></th>
                        <th scope="col">{t('term.client')}</th>
                        <th scope="col">{t('term.date')}</th>
                        <th scope="col">{t('term.category')}</th>
                        <th scope="col">{t('term.description')}</th>
                        <th scope="col">{t('term.price')}</th>
                        <th scope="col">{t('term.actions')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {pending.items.map((item, index) => (
                        <tr key={item.id}>
                            <td>
                                <input
                                    type="checkbox"
                                    checked={item.selected || false}
                                    onChange={() => handleCheckboxChange(index)}
                                />
                            </td>
                            <td>{item.client.name} ({item.client.email}) ({item.client.lang})</td>
                            <td>{item.dateFormatted}</td>
                            <td>{item.category}</td>
                            <td>{item.description}</td>
                            <td>{item.priceFormatted}</td>
                            <td>
                                <div>
                                    <button className="btn btn-sm btn-primary" data-bs-toggle="modal"
                                            onClick={() => showEdit(item)}
                                            data-bs-target="#editModal">{t('button.edit')}</button>
                                    <button className="btn btn-sm btn-danger"
                                            onClick={() => deleteOne(item)}>{t('button.delete')}</button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>

                <button type="button" className="float-end btn btn-success"
                        onClick={showBillSelected}>{t('button.billSelected')}</button>

                <hr/>

                <h1>{t('term.billed')}</h1>

                <Pagination className="float-end" pagination={billed.pagination}
                            onChangePage={(event) => refreshBilled(event.pageId)}/>

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">{t('term.invoiceId')}</th>
                        <th scope="col">{t('term.client')}</th>
                        <th scope="col">{t('term.date')}</th>
                        <th scope="col">{t('term.category')}</th>
                        <th scope="col">{t('term.description')}</th>
                        <th scope="col">{t('term.price')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {billed.items.map(item => (
                        <tr key={item.id}>
                            <td>{item.invoiceId}</td>
                            <td>{item.client.name} ({item.client.email}) ({item.client.lang})</td>
                            <td>{item.dateFormatted}</td>
                            <td>{item.category}</td>
                            <td>{item.description}</td>
                            <td>{item.priceFormatted}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default ItemsList
