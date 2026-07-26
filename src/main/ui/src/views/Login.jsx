import React, {useState} from 'react'
import ErrorResults from '../components/ErrorResults'
import service, {showSuccess} from '../service'
import {t} from '../utils/TranslationUtils'

function Login({onLoggedIn}) {
    const [activeTab, setActiveTab] = useState('password')
    const [loginForm, setLoginForm] = useState({})
    const [codeRequestForm, setCodeRequestForm] = useState({})
    const [codeForm, setCodeForm] = useState({})
    const [codeStep, setCodeStep] = useState('request')
    const [formResult, setFormResult] = useState({})

    const login = async () => {
        setFormResult({})
        try {
            const response = await service.userLogin(loginForm)
            setFormResult(response.data)
            if (response.data.success) {
                onLoggedIn()
            }
        } catch (error) {
            console.error('Error logging in', error)
        }
    }

    const requestCode = async () => {
        setFormResult({})
        try {
            const response = await service.userLoginWithCodeRequest(codeRequestForm)
            setFormResult(response.data)
            if (response.data.success) {
                showSuccess(t('login.codeSent'))
                setCodeForm({email: codeRequestForm.email})
                setCodeStep('verify')
            }
        } catch (error) {
            console.error('Error requesting login code', error)
        }
    }

    const verifyCode = async () => {
        setFormResult({})
        try {
            const response = await service.userLoginWithCode(codeForm)
            setFormResult(response.data)
            if (response.data.success) {
                onLoggedIn()
            }
        } catch (error) {
            console.error('Error verifying login code', error)
        }
    }

    const selectTab = (tab) => {
        setActiveTab(tab)
        setFormResult({})
        setCodeStep('request')
    }

    return (
        <div className="row justify-content-center">
            <div className="col-12 col-md-6 col-lg-4">
                <h1>{t('login.title')}</h1>

                <ul className="nav nav-tabs mb-3">
                    <li className="nav-item">
                        <a
                            className={`nav-link ${activeTab === 'password' ? 'active' : ''}`}
                            href="#"
                            onClick={(e) => {
                                e.preventDefault()
                                selectTab('password')
                            }}
                        >
                            {t('login.tab.password')}
                        </a>
                    </li>
                    <li className="nav-item">
                        <a
                            className={`nav-link ${activeTab === 'code' ? 'active' : ''}`}
                            href="#"
                            onClick={(e) => {
                                e.preventDefault()
                                selectTab('code')
                            }}
                        >
                            {t('login.tab.code')}
                        </a>
                    </li>
                </ul>

                <ErrorResults formResult={formResult}/>

                {activeTab === 'password' && (
                    <div>
                        <div className="mb-3">
                            <label htmlFor="loginEmail">{t('login.email')}</label>
                            <input
                                type="text"
                                className="form-control"
                                id="loginEmail"
                                value={loginForm.email || ''}
                                onChange={(e) => setLoginForm(prev => ({...prev, email: e.target.value}))}
                                onKeyDown={(e) => e.key === 'Enter' && login()}
                                autoComplete="username"
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="loginPassword">{t('login.password')}</label>
                            <input
                                type="password"
                                className="form-control"
                                id="loginPassword"
                                value={loginForm.password || ''}
                                onChange={(e) => setLoginForm(prev => ({...prev, password: e.target.value}))}
                                onKeyDown={(e) => e.key === 'Enter' && login()}
                                autoComplete="current-password"
                            />
                        </div>
                        <button type="button" className="btn btn-primary" onClick={login}>
                            {t('login.button.login')}
                        </button>
                    </div>
                )}

                {activeTab === 'code' && codeStep === 'request' && (
                    <div>
                        <div className="mb-3">
                            <label htmlFor="codeRequestEmail">{t('login.email')}</label>
                            <input
                                type="text"
                                className="form-control"
                                id="codeRequestEmail"
                                value={codeRequestForm.email || ''}
                                onChange={(e) => setCodeRequestForm(prev => ({...prev, email: e.target.value}))}
                                onKeyDown={(e) => e.key === 'Enter' && requestCode()}
                                autoComplete="username"
                            />
                        </div>
                        <button type="button" className="btn btn-primary" onClick={requestCode}>
                            {t('login.button.requestCode')}
                        </button>
                    </div>
                )}

                {activeTab === 'code' && codeStep === 'verify' && (
                    <div>
                        <div className="mb-3">
                            <label htmlFor="codeValue">{t('login.code')}</label>
                            <input
                                type="text"
                                className="form-control"
                                id="codeValue"
                                value={codeForm.code || ''}
                                onChange={(e) => setCodeForm(prev => ({...prev, code: e.target.value}))}
                                onKeyDown={(e) => e.key === 'Enter' && verifyCode()}
                                autoComplete="one-time-code"
                            />
                        </div>
                        <button type="button" className="btn btn-primary" onClick={verifyCode}>
                            {t('login.button.verifyCode')}
                        </button>
                    </div>
                )}
            </div>
        </div>
    )
}

export default Login
