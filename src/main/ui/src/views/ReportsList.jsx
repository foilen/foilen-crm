import React, {useEffect, useState} from 'react'
import {get} from '../utils/http'
import {t} from '../utils/TranslationUtils'

function ReportsList() {
    const [reports, setReports] = useState({})

    // Function to refresh reports
    const refresh = async () => {
        console.log('Reports - Load')

        try {
            const response = await get('/api/report')
            setReports(response.data.item || {})
        } catch (error) {
            console.error('Error loading reports', error)
            setReports({})
        }
    }

    // Initial load
    useEffect(() => {
        refresh()
    }, [])

    return (
        <div className="row">
            <div className="col-12">
                <h1>{t('term.balanceByClient')}</h1>

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">{t('term.client')}</th>
                        <th scope="col">{t('term.total')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {reports.balanceByClient && reports.balanceByClient.map(item => (
                        <tr key={item.clientName}>
                            <td>{item.clientName}</td>
                            <td>{item.totalFormatted}$</td>
                        </tr>
                    ))}
                    </tbody>
                </table>

                <p>{t('term.globalBalance')}: {reports.globalBalanceFormatted}$</p>

                <h1>{t('term.itemsByCategory')}</h1>

                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">{t('term.date')}</th>
                        <th scope="col">{t('term.category')}</th>
                        <th scope="col">{t('term.total')}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {reports.itemsByCategory && reports.itemsByCategory.map(item => (
                        <tr key={`${item.monthDate}-${item.category}`}>
                            <td>{item.monthDate}</td>
                            <td>{item.category}</td>
                            <td>{item.totalFormatted}$</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default ReportsList
