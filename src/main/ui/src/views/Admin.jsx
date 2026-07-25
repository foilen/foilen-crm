import React, {useState} from 'react'
import AdminExportImport from './admin/AdminExportImport'
import AdminUsers from './admin/AdminUsers'
import {t} from '../utils/TranslationUtils'

function Admin() {
    const [activeTab, setActiveTab] = useState('exportImport')

    return (
        <div className="row">
            <div className="col-12">
                <ul className="nav nav-tabs mb-3">
                    <li className="nav-item">
                        <a
                            className={`nav-link ${activeTab === 'exportImport' ? 'active' : ''}`}
                            href="#"
                            onClick={(e) => {
                                e.preventDefault()
                                setActiveTab('exportImport')
                            }}
                        >
                            {t('admin.tab.exportImport')}
                        </a>
                    </li>
                    <li className="nav-item">
                        <a
                            className={`nav-link ${activeTab === 'user' ? 'active' : ''}`}
                            href="#"
                            onClick={(e) => {
                                e.preventDefault()
                                setActiveTab('user')
                            }}
                        >
                            {t('admin.tab.user')}
                        </a>
                    </li>
                </ul>

                {activeTab === 'exportImport' && <AdminExportImport/>}
                {activeTab === 'user' && <AdminUsers/>}
            </div>
        </div>
    )
}

export default Admin
