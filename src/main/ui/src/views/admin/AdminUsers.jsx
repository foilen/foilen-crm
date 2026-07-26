import React, {useEffect, useState} from 'react'
import ErrorResults from '../../components/ErrorResults'
import PaginationControl from '../../components/PaginationControl'
import service, {showSuccess} from '../../service'
import {t} from '../../utils/TranslationUtils'

function AdminUsers() {
    const [queries, setQueries] = useState({})
    const [search, setSearch] = useState('')
    const [items, setItems] = useState([])
    const [pagination, setPagination] = useState({
        currentPageUi: 1,
        totalPages: 1,
        firstPage: true,
        lastPage: true,
    })
    const [formResult, setFormResult] = useState({})

    // Function to refresh the list of users
    const refresh = async (pageId) => {
        if (pageId === undefined) {
            pageId = 1
        }

        setQueries(prev => ({
            ...prev,
            pageId
        }))

        console.log('Admin Users - Load', {pageId, search})

        try {
            const response = await service.userListAll(pageId, search)
            setPagination(response.data.pagination)
            setItems(response.data.items || [])
        } catch (error) {
            console.error('Error loading users', error)
            setItems([])
        }
    }

    // Function to toggle the admin status of a user
    const toggleAdmin = async (item) => {
        const nextAdmin = !item.admin
        const confirmMessage = nextAdmin
            ? t('prompt.makeAdmin.confirm', {0: item.email})
            : t('prompt.removeAdmin.confirm', {0: item.email})

        if (!window.confirm(confirmMessage)) {
            return
        }

        setFormResult({})
        console.log('Admin Users - Update Admin', item.email, nextAdmin)

        try {
            const response = await service.userUpdateAdmin(item.email, {admin: nextAdmin})
            setFormResult(response.data)
            if (response.data.success) {
                showSuccess(t('prompt.updateAdmin.success', {0: item.email}))
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error updating user admin status', error)
        }
    }

    // Function to toggle the disabled status of a user
    const toggleDisabled = async (item) => {
        const nextDisabled = !item.disabled
        const confirmMessage = nextDisabled
            ? t('prompt.disable.confirm', {0: item.email})
            : t('prompt.enable.confirm', {0: item.email})

        if (!window.confirm(confirmMessage)) {
            return
        }

        setFormResult({})
        console.log('Admin Users - Update Disabled', item.email, nextDisabled)

        try {
            const response = await service.userUpdateDisabled(item.email, {disabled: nextDisabled})
            setFormResult(response.data)
            if (response.data.success) {
                showSuccess(t('prompt.updateDisabled.success', {0: item.email}))
                refresh(queries.pageId)
            }
        } catch (error) {
            console.error('Error updating user disabled status', error)
        }
    }

    // Initial load
    useEffect(() => {
        refresh()
    }, [])

    return (
        <div className="row">
            <div className="col-12">
                <ErrorResults formResult={formResult}/>

                <div className="mb-3">
                    <input
                        type="text"
                        className="form-control"
                        placeholder={t('term.search')}
                        value={search}
                        autoComplete="off"
                        onChange={(e) => setSearch(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                refresh(1)
                            }
                        }}
                    />
                </div>

                <PaginationControl className="float-end" state={pagination}
                            onPageChange={(newPageId) => refresh(newPageId)}/>

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">{t('term.email')}</th>
                        <th scope="col">{t('term.isAdmin')}</th>
                        <th scope="col">{t('term.isDisabled')}</th>
                        <th scope="col">{t('term.actions')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {items.map(item => (
                        <tr key={item.email}>
                            <td>{item.email && <a href={`mailto:${item.email}`}>{item.email}</a>}</td>
                            <td>{item.admin ? t('common.yes') : t('common.no')}</td>
                            <td>{item.disabled ? t('common.yes') : t('common.no')}</td>
                            <td>
                                {item.admin ? (
                                    <button className="btn btn-sm btn-danger"
                                            onClick={() => toggleAdmin(item)}>{t('button.removeAdmin')}</button>
                                ) : (
                                    <button className="btn btn-sm btn-primary"
                                            onClick={() => toggleAdmin(item)}>{t('button.makeAdmin')}</button>
                                )}
                                {item.disabled ? (
                                    <button className="btn btn-sm btn-success"
                                            onClick={() => toggleDisabled(item)}>{t('button.enable')}</button>
                                ) : (
                                    <button className="btn btn-sm btn-danger"
                                            onClick={() => toggleDisabled(item)}>{t('button.disable')}</button>
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default AdminUsers
