import React, {useEffect, useState} from 'react'
import ErrorResults from '../components/ErrorResults'
import Pagination from '../components/Pagination'
import TechnicalSupportSelect from '../components/TechnicalSupportSelect'
import {delete as del, get, post, put, showSuccess} from '../utils/http'
import {t} from '../utils/TranslationUtils'

function ClientsList() {
    const [queries, setQueries] = useState({})
    const [items, setItems] = useState([])
    const [pagination, setPagination] = useState({
        currentPageUi: 1,
        totalPages: 1,
        firstPage: true,
        lastPage: true,
    })
    const [createForm, setCreateForm] = useState({})
    const [editForm, setEditForm] = useState({
        clientShortName: null,
        technicalSupportSid: null,
    })
    const [formResult, setFormResult] = useState({})

    // Function to refresh the list of clients
    const refresh = async (pageId) => {
        if (pageId === undefined) {
            pageId = 1
        }

        setQueries(prev => ({
            ...prev,
            pageId
        }))

        console.log('Clients - Load', {pageId})

        try {
            const response = await get('/api/client/listAll', {pageId})
            setPagination(response.data.pagination)
            setItems(response.data.items || [])
        } catch (error) {
            console.error('Error loading clients', error)
            setItems([])
        }
    }

    // Function to show create modal
    const showCreate = () => {
        setFormResult({})
    }

    // Function to create a new client
    const create = async () => {
        setFormResult({})
        console.log('Clients - Create', createForm)

        try {
            const response = await post('/api/client', createForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#createModal .btn-secondary').click()
                showSuccess(t('prompt.create.success', {0: createForm.shortName}))
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error creating client', error)
        }
    }

    // Function to delete a client
    const deleteOne = async (item) => {
        if (window.confirm(t('prompt.delete.confirm', {0: item.name}))) {
            console.log('Client - Delete', item.name)

            try {
                await del('/api/client/' + item.shortName)
                showSuccess(t('prompt.delete.success', {0: item.shortName}))
                refresh(queries.pageId)
            } catch (error) {
                console.error('Error deleting client', error)
            }
        }
    }

    // Function to show edit modal
    const showEdit = (item) => {
        setFormResult({})
        setEditForm({
            ...item,
            clientShortName: item.shortName,
            technicalSupportSid: item.technicalSupport ? item.technicalSupport.sid : null
        })
    }

    // Function to edit a client
    const edit = async () => {
        setFormResult({})
        console.log('Clients - Edit', editForm)

        try {
            const response = await put('/api/client/' + editForm.clientShortName, editForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#editModal .btn-secondary').click()
                showSuccess(t('prompt.edit.success', {0: editForm.name}))
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error editing client', error)
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
                                    <label htmlFor="name">{t('term.name')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="name"
                                        value={createForm.name || ''}
                                        onChange={(e) => handleCreateFormChange('name', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.name && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.name.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="shortName">{t('term.shortName')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="shortName"
                                        value={createForm.shortName || ''}
                                        onChange={(e) => handleCreateFormChange('shortName', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.shortName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.shortName.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="contactName">{t('term.contactName')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="contactName"
                                        value={createForm.contactName || ''}
                                        onChange={(e) => handleCreateFormChange('contactName', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.contactName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.contactName.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="email">{t('term.email')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="email"
                                        value={createForm.email || ''}
                                        onChange={(e) => handleCreateFormChange('email', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.email && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.email.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="address">{t('term.address')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="address"
                                        value={createForm.address || ''}
                                        onChange={(e) => handleCreateFormChange('address', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.address && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.address.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="tel">{t('term.tel')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="tel"
                                        value={createForm.tel || ''}
                                        onChange={(e) => handleCreateFormChange('tel', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.tel && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.tel.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="mainSite">{t('term.mainSite')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="mainSite"
                                        value={createForm.mainSite || ''}
                                        onChange={(e) => handleCreateFormChange('mainSite', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.mainSite && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.mainSite.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="lang">{t('term.lang')}</label>
                                    <select
                                        className="form-control"
                                        id="lang"
                                        value={createForm.lang || ''}
                                        onChange={(e) => handleCreateFormChange('lang', e.target.value)}
                                        autoComplete="off"
                                    >
                                        <option value=""></option>
                                        <option>FR</option>
                                        <option>EN</option>
                                    </select>
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.lang && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.lang.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="technicalSupportSid">{t('term.technicalSupportSid')}</label>
                                    <TechnicalSupportSelect
                                        id="technicalSupportSid"
                                        value={createForm.technicalSupportSid || ''}
                                        onChange={(value) => handleCreateFormChange('technicalSupportSid', value)}
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.technicalSupportSid && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.technicalSupportSid.map((errorCode, index) => (
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
                                    <label htmlFor="name2">{t('term.name')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="name2"
                                        value={editForm.name || ''}
                                        onChange={(e) => handleEditFormChange('name', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.name && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.name.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="shortName2">{t('term.shortName')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="shortName2"
                                        value={editForm.shortName || ''}
                                        onChange={(e) => handleEditFormChange('shortName', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.shortName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.shortName.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="contactName2">{t('term.contactName')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="contactName2"
                                        value={editForm.contactName || ''}
                                        onChange={(e) => handleEditFormChange('contactName', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.contactName && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.contactName.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="email2">{t('term.email')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="email2"
                                        value={editForm.email || ''}
                                        onChange={(e) => handleEditFormChange('email', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.email && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.email.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="address2">{t('term.address')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="address2"
                                        value={editForm.address || ''}
                                        onChange={(e) => handleEditFormChange('address', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.address && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.address.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="tel2">{t('term.tel')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="tel2"
                                        value={editForm.tel || ''}
                                        onChange={(e) => handleEditFormChange('tel', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.tel && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.tel.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="mainSite2">{t('term.mainSite')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="mainSite2"
                                        value={editForm.mainSite || ''}
                                        onChange={(e) => handleEditFormChange('mainSite', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.mainSite && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.mainSite.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="lang2">{t('term.lang')}</label>
                                    <select
                                        className="form-control"
                                        id="lang2"
                                        value={editForm.lang || ''}
                                        onChange={(e) => handleEditFormChange('lang', e.target.value)}
                                        autoComplete="off"
                                    >
                                        <option value=""></option>
                                        <option>FR</option>
                                        <option>EN</option>
                                    </select>
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.lang && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.lang.map((errorCode, index) => (
                                                <p key={index}>{errorCode}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="technicalSupportSid2">{t('term.technicalSupportSid')}</label>
                                    <TechnicalSupportSelect
                                        id="technicalSupportSid2"
                                        value={editForm.technicalSupportSid || ''}
                                        onChange={(value) => handleEditFormChange('technicalSupportSid', value)}
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.technicalSupportSid && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.technicalSupportSid.map((errorCode, index) => (
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
                        <th scope="col">{t('term.name')}</th>
                        <th scope="col">{t('term.shortName')}</th>
                        <th scope="col">{t('term.contactName')}</th>
                        <th scope="col">{t('term.email')}</th>
                        <th scope="col">{t('term.address')}</th>
                        <th scope="col">{t('term.tel')}</th>
                        <th scope="col">{t('term.mainSite')}</th>
                        <th scope="col">{t('term.lang')}</th>
                        <th scope="col">{t('term.sid')}</th>
                        <th scope="col">{t('term.pricePerHour')}</th>
                        <th scope="col">{t('term.actions')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {items.map(item => (
                        <tr key={item.shortName}>
                            <td>{item.name}</td>
                            <td>{item.shortName}</td>
                            <td>{item.contactName}</td>
                            <td><a href={`mailto:${item.email}`}>{item.email}</a></td>
                            <td>{item.address}</td>
                            <td>{item.tel}</td>
                            <td><a href={item.mainSite} target="_blank" rel="noopener noreferrer">{item.mainSite}</a>
                            </td>
                            <td>{item.lang}</td>
                            <td>{item.technicalSupport ? item.technicalSupport.sid : ''}</td>
                            <td>{item.technicalSupport ? `${item.technicalSupport.pricePerHourFormatted}$` : ''}</td>
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

export default ClientsList
