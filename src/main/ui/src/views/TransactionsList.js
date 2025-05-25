import React, {useEffect, useState} from 'react'
import ErrorResults from '../components/ErrorResults'
import Pagination from '../components/Pagination'
import ClientSelect from '../components/ClientSelect'
import {get, post, put, showSuccess} from '../utils/http'
import {dateNowDayOnly, priceToLong} from '../utils/features'
import {t} from '../utils/TranslationUtils'

function TransactionsList() {
    const [queries, setQueries] = useState({})
    const [items, setItems] = useState([])
    const [pagination, setPagination] = useState({
        currentPageUi: 1,
        totalPages: 1,
        firstPage: true,
        lastPage: true,
    })
    const [form, setForm] = useState({
        date: dateNowDayOnly(),
        clientShortName: '',
        paymentType: '',
        price: '',
    })
    const [editForm, setEditForm] = useState({
        clientShortName: null,
    })
    const [formResult, setFormResult] = useState({})

    // Function to handle form field changes
    const handleFormChange = (field, value) => {
        setForm(prevForm => ({
            ...prevForm,
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

    // Function to create a new payment
    const create = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(form))
        clonedForm.price = priceToLong(clonedForm.price)
        console.log('Transaction Payment - Create', clonedForm)

        try {
            const response = await post('/api/transaction/payment', clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                // Close the modal
                document.querySelector('#createModal .btn-secondary').click()
                // Refresh the list
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error creating payment', error)
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
            clientShortName: item.client ? item.client.shortName : null,
            paymentType: item.description
        })
    }

    // Function to edit a transaction
    const edit = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(editForm))
        clonedForm.price = priceToLong(clonedForm.price)
        console.log('Transaction - Edit', clonedForm)

        try {
            const response = await put('/api/transaction/' + editForm.id, clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#editModal .btn-secondary').click()
                showSuccess(t('prompt.edit.success', {0: editForm.id}))
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error editing transaction', error)
        }
    }

    // Function to refresh the list of transactions
    const refresh = async (pageId) => {
        if (pageId === undefined) {
            pageId = 1
        }

        setQueries(prevQueries => ({
            ...prevQueries,
            pageId
        }))

        console.log('Transactions - Load', {pageId})

        try {
            const response = await get('/api/transaction/listAll', {pageId})
            setPagination(response.data.pagination)
            setItems(response.data.items || [])
        } catch (error) {
            console.error('Error loading transactions', error)
            setItems([])
        }
    }

    // Initial load
    useEffect(() => {
        refresh()
    }, [])

    return (
        <div className="row">
            <div className="col-12">
                <button type="button" className="btn btn-success" data-bs-toggle="modal" data-bs-target="#createModal">
                    {t('button.createPayment')}
                </button>

                <div className="modal fade" id="createModal" tabIndex="-1" role="dialog"
                     aria-labelledby="createModalLabel"
                     aria-hidden="true">
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
                                        value={form.clientShortName}
                                        onChange={(value) => handleFormChange('clientShortName', value)}
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.clientShortName.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
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
                                        value={form.date}
                                        onChange={(e) => handleFormChange('date', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.date && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.date.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="paymentType">{t('term.paymentType')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="paymentType"
                                        value={form.paymentType}
                                        onChange={(e) => handleFormChange('paymentType', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.paymentType && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.paymentType.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
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
                                        value={form.price}
                                        onChange={(e) => handleFormChange('price', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.price && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.price.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
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
                                <h5 className="modal-title" id="editModalLabel">{t('button.edit')}</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <ErrorResults formResult={formResult}/>

                                <div className="mb-3">
                                    <label htmlFor="clientShortName2">{t('term.clientShortName')}</label>
                                    <ClientSelect
                                        id="clientShortName2"
                                        value={editForm.clientShortName || ''}
                                        onChange={(value) => handleEditFormChange('clientShortName', value)}
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.clientShortName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.clientShortName.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="date2">{t('term.date')}</label>
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
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="paymentType2">{t('term.paymentType')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="paymentType2"
                                        value={editForm.paymentType || ''}
                                        onChange={(e) => handleEditFormChange('paymentType', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.paymentType && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.paymentType.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="price2">{t('term.price')}</label>
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
                                                <p key={index}>{errorCode}</p>
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
            </div>

            <div className="col-12">
                <Pagination
                    className="float-end"
                    pagination={pagination}
                    onChangePage={(event) => refresh(event.pageId)}
                />

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">{t('term.invoiceId')}</th>
                        <th scope="col">{t('term.client')}</th>
                        <th scope="col">{t('term.date')}</th>
                        <th scope="col">{t('term.description')}</th>
                        <th scope="col">{t('term.price')}</th>
                        <th scope="col">{t('term.actions')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {items.map(item => (
                        <tr key={item.id}>
                            <td>{item.invoiceId}</td>
                            <td>{item.client.name} ({item.client.email}) ({item.client.lang})</td>
                            <td>{item.dateFormatted}</td>
                            <td>{item.description}</td>
                            <td>{item.priceFormatted}</td>
                            <td>
                                <div>
                                    {!item.invoiceId && (
                                        <button className="btn btn-sm btn-primary" data-bs-toggle="modal"
                                                onClick={() => showEdit(item)}
                                                data-bs-target="#editModal">{t('button.edit')}</button>
                                    )}
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default TransactionsList
