import React, {useEffect, useState} from 'react'
import ErrorResults from '../components/ErrorResults'
import Pagination from '../components/Pagination'
import ClientSelect from '../components/ClientSelect'
import CategorySelect from '../components/CategorySelect'
import {delete as del, get, post, put, showSuccess} from '../utils/http'
import {dateNowDayOnly, priceToLong} from '../utils/features'
import {t} from '../utils/TranslationUtils'

function RecurrentItemsList() {
    const [queries, setQueries] = useState({})
    const [items, setItems] = useState([])
    const [frequencies] = useState([
        [2, 'recurrence.monthly'],
        [1, 'recurrence.yearly'],
    ])
    const [pagination, setPagination] = useState({
        currentPageUi: 1,
        totalPages: 1,
        firstPage: true,
        lastPage: true,
    })
    const [createForm, setCreateForm] = useState({
        nextGenerationDate: dateNowDayOnly(),
        calendarUnit: '',
        delta: '',
        clientShortName: '',
        description: '',
        price: '',
        category: ''
    })
    const [editForm, setEditForm] = useState({
        id: '',
        calendarUnit: '',
        delta: '',
        nextGenerationDate: '',
        clientShortName: '',
        description: '',
        price: '',
        category: ''
    })
    const [formResult, setFormResult] = useState({})

    // Function to handle create form field changes
    const handleCreateFormChange = (field, value) => {
        setCreateForm(prevForm => ({
            ...prevForm,
            [field]: value
        }))
    }

    // Function to handle edit form field changes
    const handleEditFormChange = (field, value) => {
        setEditForm(prevForm => ({
            ...prevForm,
            [field]: value
        }))
    }

    // Function to refresh the list of recurrent items
    const refresh = async (pageId) => {
        if (pageId === undefined) {
            pageId = 1
        }

        setQueries(prevQueries => ({
            ...prevQueries,
            pageId
        }))

        console.log('Recurrent Items - Load', {pageId})

        try {
            const response = await get('/api/recurrentItem/listAll', {pageId})
            setPagination(response.data.pagination)
            setItems(response.data.items || [])
        } catch (error) {
            console.error('Error loading recurrent items', error)
            setItems([])
        }
    }

    // Function to show create modal
    const showCreate = () => {
        setFormResult({})
    }

    // Function to create a new recurrent item
    const create = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(createForm))
        clonedForm.price = priceToLong(clonedForm.price)
        console.log('Recurrent Item - Create', clonedForm)

        try {
            const response = await post('/api/recurrentItem', clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#createModal .btn-secondary').click()
                showSuccess(`Successfully created ${createForm.description}`)
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error creating recurrent item', error)
        }
    }

    // Function to delete a recurrent item
    const deleteOne = async (item) => {
        if (window.confirm(`Are you sure you want to delete ${item.id}?`)) {
            console.log('Recurrent Item - Delete', item.id)

            try {
                await del('/api/recurrentItem/' + item.id)
                showSuccess(`Successfully deleted ${item.description}`)
                refresh()
            } catch (error) {
                console.error('Error deleting recurrent item', error)
            }
        }
    }

    // Function to show edit modal
    const showEdit = (item) => {
        setFormResult({})
        setEditForm({
            id: item.id,
            calendarUnit: item.calendarUnit,
            delta: item.delta,
            nextGenerationDate: item.nextGenerationDateFormatted,
            clientShortName: item.client ? item.client.shortName : '',
            description: item.description,
            price: item.priceFormatted,
            category: item.category
        })
    }

    // Function to edit a recurrent item
    const edit = async () => {
        setFormResult({})
        const clonedForm = JSON.parse(JSON.stringify(editForm))
        clonedForm.price = priceToLong(clonedForm.price)
        console.log('Recurrent Item - Edit', clonedForm)

        try {
            const response = await put('/api/recurrentItem/' + clonedForm.id, clonedForm)
            setFormResult(response.data)
            if (response.data.success) {
                document.querySelector('#editModal .btn-secondary').click()
                showSuccess(`Successfully edited ${editForm.description}`)
                refresh()
            }
        } catch (error) {
            console.error('Error editing recurrent item', error)
        }
    }

    // Initial load
    useEffect(() => {
        refresh()
    }, [])

    return (
        <div className="row">
            <div className="col-12">
                <button type="button" className="btn btn-success" data-bs-toggle="modal" onClick={showCreate}
                        data-bs-target="#createModal">
                    {t('button.create')}
                </button>

                <Pagination className="float-end" pagination={pagination}
                            onChangePage={(event) => refresh(event.pageId)}/>

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
                                    <label htmlFor="calendarUnit">{t('term.frequency')}</label>
                                    <select className="form-control" id="calendarUnit" value={createForm.calendarUnit}
                                            onChange={(e) => handleCreateFormChange('calendarUnit', e.target.value)}>
                                        <option value="">{t('term.frequency')}</option>
                                        {frequencies.map(frequency => (
                                            <option key={frequency[0]} value={frequency[0]}>
                                                {t(frequency[1])}
                                            </option>
                                        ))}
                                    </select>
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.calendarUnit && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.calendarUnit.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="delta">{t('term.delta')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="delta"
                                        value={createForm.delta}
                                        onChange={(e) => handleCreateFormChange('delta', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.delta && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.delta.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="nextGenerationDate">{t('term.nextGenerationDate')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="nextGenerationDate"
                                        value={createForm.nextGenerationDate}
                                        onChange={(e) => handleCreateFormChange('nextGenerationDate', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.nextGenerationDate && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.nextGenerationDate.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="clientShortName">{t('term.clientShortName')}</label>
                                    <ClientSelect
                                        id="clientShortName"
                                        value={createForm.clientShortName}
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
                                    <label htmlFor="description">{t('term.description')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="description"
                                        value={createForm.description}
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
                                        value={createForm.price}
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
                                    <CategorySelect
                                        id="category"
                                        value={createForm.category}
                                        onChange={(value) => handleCreateFormChange('category', value)}
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
                                        data-bs-dismiss="modal">{t('button.close')}
                                </button>
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
                                    <label htmlFor="calendarUnit2">{t('term.frequency')}</label>
                                    <select className="form-control" id="calendarUnit2" value={editForm.calendarUnit}
                                            onChange={(e) => handleEditFormChange('calendarUnit', e.target.value)}>
                                        <option value="">{t('term.frequency')}</option>
                                        {frequencies.map(frequency => (
                                            <option key={frequency[0]} value={frequency[0]}>
                                                {t(frequency[1])}
                                            </option>
                                        ))}
                                    </select>
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.calendarUnit && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.calendarUnit.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="delta2">{t('term.delta')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="delta2"
                                        value={editForm.delta}
                                        onChange={(e) => handleEditFormChange('delta', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.delta && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.delta.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="nextGenerationDate2">{t('term.nextGenerationDate')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="nextGenerationDate2"
                                        value={editForm.nextGenerationDate}
                                        onChange={(e) => handleEditFormChange('nextGenerationDate', e.target.value)}
                                        autoComplete="off"
                                    />
                                    {formResult.validationErrorsByField && formResult.validationErrorsByField.nextGenerationDate && (
                                        <div className="text-danger">
                                            {formResult.validationErrorsByField.nextGenerationDate.map((errorCode, index) => (
                                                <p key={index}>{t(errorCode)}</p>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="clientShortName2">{t('term.clientShortName')}</label>
                                    <ClientSelect
                                        id="clientShortName2"
                                        value={editForm.clientShortName}
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
                                    <label htmlFor="description2">{t('term.description')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="description2"
                                        value={editForm.description}
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
                                    <label htmlFor="price2">{t('term.price')}</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="price2"
                                        value={editForm.price}
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
                                    <label htmlFor="category2">{t('term.category')}</label>
                                    <CategorySelect
                                        id="category2"
                                        value={editForm.category}
                                        onChange={(value) => handleEditFormChange('category', value)}
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
                                        data-bs-dismiss="modal">{t('button.close')}
                                </button>
                                <button type="button" className="btn btn-success"
                                        onClick={edit}>{t('button.edit')}</button>
                            </div>
                        </div>
                    </div>
                </div>

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">{t('term.client')}</th>
                        <th scope="col">{t('term.category')}</th>
                        <th scope="col">{t('term.description')}</th>
                        <th scope="col">{t('term.price')}</th>
                        <th scope="col">{t('term.frequency')}</th>
                        <th scope="col">{t('term.delta')}</th>
                        <th scope="col">{t('term.nextGenerationDate')}</th>
                        <th scope="col">{t('term.actions')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {items.map(item => (
                        <tr key={item.id}>
                            <td>{item.client.name} ({item.client.email}) ({item.client.lang})</td>
                            <td>{item.category}</td>
                            <td>{item.description}</td>
                            <td>{item.priceFormatted}</td>
                            <td>{item.calendarUnitCode === 'recurrence.monthly' ? 'Monthly' : 'Yearly'}</td>
                            <td>{item.delta}</td>
                            <td>{item.nextGenerationDateFormatted}</td>
                            <td>
                                <div>
                                    <button className="btn btn-sm btn-primary" data-bs-toggle="modal"
                                            onClick={() => showEdit(item)}
                                            data-bs-target="#editModal">{t('button.edit')}
                                    </button>
                                    <button className="btn btn-sm btn-danger"
                                            onClick={() => deleteOne(item)}>{t('button.delete')}
                                    </button>
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

export default RecurrentItemsList
