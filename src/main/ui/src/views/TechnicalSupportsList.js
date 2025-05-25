import React, {useEffect, useState} from 'react'
import ErrorResults from '../components/ErrorResults'
import Pagination from '../components/Pagination'
import {delete as del, get, post, put, showSuccess} from '../utils/http'
import {priceToLong} from '../utils/features'
import {t} from '../utils/TranslationUtils'

function TechnicalSupportsList() {
    const [queries, setQueries] = useState({})
    const [items, setItems] = useState([])
    const [pagination, setPagination] = useState({
        currentPageUi: 1,
        totalPages: 1,
        firstPage: true,
        lastPage: true,
    })
    const [createForm, setCreateForm] = useState({})
    const [editForm, setEditForm] = useState({})
    const [formResult, setFormResult] = useState({})

    // Function to refresh the list of technical supports
    const refresh = async (pageId) => {
        if (pageId === undefined) {
            pageId = 1
        }

        setQueries(prev => ({
            ...prev,
            pageId
        }))

        console.log('Technical Supports - Load', {pageId})

        try {
            const response = await get('/api/technicalSupport/listAll', {pageId})
            setPagination(response.data.pagination)
            setItems(response.data.items || [])
        } catch (error) {
            console.error('Error loading technical supports', error)
            setItems([])
        }
    }

    // Function to show create modal
    const showCreate = () => {
        setFormResult({})
    }

    // Function to create a new technical support
    const create = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(createForm))
        clonedForm.pricePerHour = priceToLong(clonedForm.pricePerHour)
        console.log('Technical Support - Create', clonedForm)

        try {
            const response = await post('/api/technicalSupport', clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#createModal .btn-secondary').click()
                showSuccess(`Successfully created ${createForm.sid}`)
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error creating technical support', error)
        }
    }

    // Function to delete a technical support
    const deleteOne = async (item) => {
        if (window.confirm(`Are you sure you want to delete ${item.sid}?`)) {
            console.log('Technical Support - Delete', item.sid)

            try {
                await del('/api/technicalSupport/' + item.sid)
                showSuccess(`Successfully deleted ${item.sid}`)
                refresh(queries.pageId)
            } catch (error) {
                console.error('Error deleting technical support', error)
            }
        }
    }

    // Function to show edit modal
    const showEdit = (item) => {
        setFormResult({})
        setEditForm({
            ...item,
            id: item.sid,
            pricePerHour: item.pricePerHourFormatted
        })
    }

    // Function to edit a technical support
    const edit = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(editForm))
        clonedForm.pricePerHour = priceToLong(clonedForm.pricePerHour)
        console.log('Technical Support - Edit', clonedForm)

        try {
            const response = await put('/api/technicalSupport/' + clonedForm.id, clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#editModal .btn-secondary').click()
                showSuccess(`Successfully edited ${editForm.sid}`)
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error editing technical support', error)
        }
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

    // Initial load
    useEffect(() => {
        refresh()
    }, [])

    return (
        <div className="row">
            <div className="col-12">
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
                                    <label htmlFor="sid">{t('term.sid')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="sid"
                                        value={createForm.sid || ''}
                                        onChange={(e) => handleCreateFormChange('sid', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.sid && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.sid.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="pricePerHour">{t('term.pricePerHour')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="pricePerHour"
                                        value={createForm.pricePerHour || ''}
                                        onChange={(e) => handleCreateFormChange('pricePerHour', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.pricePerHour && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.pricePerHour.map((errorCode, index) => (
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
                                    <label htmlFor="sid2">{t('term.sid')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="sid2"
                                        value={editForm.sid || ''}
                                        onChange={(e) => handleEditFormChange('sid', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.sid && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.sid.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="pricePerHour2">{t('term.pricePerHour')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="pricePerHour2"
                                        value={editForm.pricePerHour || ''}
                                        onChange={(e) => handleEditFormChange('pricePerHour', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.pricePerHour && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.pricePerHour.map((errorCode, index) => (
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

                <Pagination className="float-end" pagination={pagination}
                            onChangePage={(event) => refresh(event.pageId)}/>

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">{t('term.sid')}</th>
                        <th scope="col">{t('term.pricePerHour')}</th>
                        <th scope="col">{t('term.actions')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {items.map(item => (
                        <tr key={item.sid}>
                            <td>{item.sid}</td>
                            <td>{item.pricePerHourFormatted}$</td>
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
            </div>
        </div>
    )
}

export default TechnicalSupportsList
