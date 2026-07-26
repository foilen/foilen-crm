import React, {useEffect, useState} from 'react'
import {NavLink, Route, Routes} from 'react-router-dom'
import axios from 'axios'
import {ToastContainer} from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

// Import components
import Home from './views/Home'
import Login from './views/Login'
import Admin from './views/Admin'
import MyAccount from './views/account/MyAccount'
import ClientsList from './views/ClientsList'
import ItemsList from './views/ItemsList'
import RecurrentItemsList from './views/RecurrentItemsList'
import ReportsList from './views/ReportsList'
import TechnicalSupportsList from './views/TechnicalSupportsList'
import TransactionsList from './views/TransactionsList'
import service from './service'
import {t, updateAppDetails} from "./utils/TranslationUtils"

function App() {

    const [appDetails, setAppDetails] = useState({
        lang: 'en',
        userId: '',
        userEmail: '',
        userAdmin: false,
        version: '',
        translations: {en: {}, fr: {}}
    })
    const [currentLang, setCurrentLang] = useState('en')

    // Function to change language
    const changeLanguage = (nextLang) => {
        // Update the app details with the new language
        setAppDetails(prevDetails => {
            const newDetails = {...prevDetails, lang: nextLang}
            // Update the global appDetails for translations
            updateAppDetails(newDetails)
            return newDetails
        })
        setCurrentLang(nextLang)

        // Persist language change
        axios.get(`/?lang=${nextLang}`)
    }

    // Function to refresh app details
    const refresh = () => {
        service.appDetails()
            .then(response => {
                const details = response.data.item
                setAppDetails(details)
                setCurrentLang(details.lang)
                updateAppDetails(details)
            })
            .catch(error => {
                console.error('Error fetching app details:', error)
            })
    }

    // Function to log out
    const logout = async () => {
        try {
            await service.userLogout()
        } catch (error) {
            console.error('Error logging out', error)
        }
        refresh()
    }

    // Equivalent to mounted lifecycle hook
    useEffect(() => {
        refresh()
    }, [])

    // Check for ?continue in URL and redirect if needed
    useEffect(() => {
        const url = new URL(window.location.href)
        if (url.search === '?continue') {
            // Create a clean URL without the ?continue parameter
            const cleanUrl = `${url.origin}${url.hash}`
            window.location.href = cleanUrl
        }
    }, [])

    if (!appDetails.userEmail) {
        return (
            <div className="container-fluid" id="app">
                <ToastContainer/>
                <div className="row">
                    <div className="col-12">
                        <span className="navbar-brand mb-0 h1">Foilen CRM</span>
                    </div>
                </div>
                <Login onLoggedIn={refresh}/>
            </div>
        )
    }

    return (
        <div className="container-fluid" id="app">
            {/* Errors and success messages */}
            <div className="row">
                <div id="errors"></div>
                <div id="successes"></div>
            </div>
            <ToastContainer/>

            {/* Nav Bar */}
            <nav id="main-nav" className="navbar navbar-expand-sm navbar-light bg-light">
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                        data-bs-target="#navbarToggler" aria-controls="navbarToggler" aria-expanded="false"
                        aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <span className="navbar-brand mb-0 h1">Foilen CRM</span>

                <div className="collapse navbar-collapse" id="navbarToggler">
                    <ul className="navbar-nav me-auto">
                        <li className="nav-item">
                            <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`} to="/">
                                {t('menu.home')}
                            </NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`} to="/clients">
                                {t('menu.clients')}
                            </NavLink>
                        </li>
                        {appDetails.userAdmin && (
                            <li className="nav-item">
                                <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}
                                         to="/technicalSupports">
                                    {t('menu.technicalSupports')}
                                </NavLink>
                            </li>
                        )}
                        <li className="nav-item">
                            <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`} to="/items">
                                {t('menu.items')}
                            </NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}
                                     to="/transactions">
                                {t('menu.transactions')}
                            </NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}
                                     to="/recurrentItems">
                                {t('menu.recurrentItems')}
                            </NavLink>
                        </li>
                        {appDetails.userAdmin && (
                            <li className="nav-item">
                                <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}
                                         to="/reports">
                                    {t('menu.reports')}
                                </NavLink>
                            </li>
                        )}
                        {appDetails.userAdmin && (
                            <li className="nav-item">
                                <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}
                                         to="/admin">
                                    {t('menu.admin')}
                                </NavLink>
                            </li>
                        )}
                    </ul>
                    <ul className="navbar-nav">
                        <li className="nav-item">
                            {currentLang === 'en' && <a className="nav-link"
                                                        onClick={() => changeLanguage('fr')}> Français</a>}
                            {currentLang !== 'en' && <a className="nav-link"
                                                        onClick={() => changeLanguage('en')}> English</a>}
                        </li>
                        <li className="nav-item">
                            <NavLink className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}
                                     to="/account">
                                {t('menu.account')}
                            </NavLink>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#" onClick={(e) => {
                                e.preventDefault()
                                logout()
                            }}>{t('menu.logout')}</a>
                        </li>
                    </ul>
                </div>
            </nav>

            {/* User details */}
            <div className="row">
                <div className="col-12" style={{textAlign: 'right'}}>
                    {appDetails.userEmail} {appDetails.userAdmin && <span>(ADMIN)</span>}
                </div>
            </div>

            {/* Content */}
            <div>
                <Routes>
                    <Route path="/" element={<Home appDetails={appDetails}/>}/>
                    <Route path="/account" element={<MyAccount appDetails={appDetails} onAppDetailsChange={refresh}/>}/>
                    <Route path="/admin" element={<Admin/>}/>
                    <Route path="/clients" element={<ClientsList appDetails={appDetails}/>}/>
                    <Route path="/items" element={<ItemsList appDetails={appDetails}/>}/>
                    <Route path="/recurrentItems" element={<RecurrentItemsList appDetails={appDetails}/>}/>
                    <Route path="/reports" element={<ReportsList/>}/>
                    <Route path="/technicalSupports" element={<TechnicalSupportsList/>}/>
                    <Route path="/transactions" element={<TransactionsList appDetails={appDetails}/>}/>
                </Routes>
            </div>

            {/* Footer */}
            <hr/>
            <footer>
                <p>
                    &copy; <a href="https://foilen.com" target="_blank" rel="noopener noreferrer">Foilen</a> 2015-2026
                </p>
                <p>Version: {appDetails.version}</p>
            </footer>
        </div>
    )
}

export default App
