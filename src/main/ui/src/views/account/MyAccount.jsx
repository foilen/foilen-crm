import React, {useState} from 'react'
import AccountChangePassword from './AccountChangePassword'
import {t} from '../../utils/TranslationUtils'

function MyAccount({appDetails, onAppDetailsChange}) {
    const [activeTab, setActiveTab] = useState('changePassword')

    return (
        <div className="row">
            <div className="col-12">
                <h1>{t('account.title')}</h1>

                <ul className="nav nav-tabs mb-3">
                    <li className="nav-item">
                        <a
                            className={`nav-link ${activeTab === 'changePassword' ? 'active' : ''}`}
                            href="#"
                            onClick={(e) => {
                                e.preventDefault()
                                setActiveTab('changePassword')
                            }}
                        >
                            {t('account.tab.changePassword')}
                        </a>
                    </li>
                </ul>

                {activeTab === 'changePassword' &&
                    <AccountChangePassword appDetails={appDetails} onAppDetailsChange={onAppDetailsChange}/>}
            </div>
        </div>
    )
}

export default MyAccount
