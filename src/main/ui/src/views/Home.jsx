import React from 'react'
import {t} from '../utils/TranslationUtils'

function Home({appDetails = {}}) {
    // Using the global t function

    // Create the documentation link element to be inserted in the translation
    const documentationLink = (
        <a href="/swagger-ui.html" target="_blank" rel="noopener noreferrer">
            {t('home.documentation')}
        </a>
    )

    return (
        <div className="row">
            <div className="col-12">
                <h1>{t('home.welcome')}</h1>
                <p>
                    {appDetails.userAdmin
                        ? t('home.description', {documentationLink})
                        : t('home.description.noAdmin')}
                </p>
                {!appDetails.userAdmin && appDetails.clientBalances && appDetails.clientBalances.length > 0 && (
                    <table className="table table-striped">
                        <thead>
                        <tr>
                            <th scope="col">{t('term.client')}</th>
                            <th scope="col">{t('term.balanceAfterLastInvoiceOrPayment')}</th>
                            <th scope="col">{t('term.currentBalance')}</th>
                        </tr>
                        </thead>
                        <tbody>
                        {appDetails.clientBalances.map(item => (
                            <tr key={item.clientName}>
                                <td>{item.clientName}</td>
                                <td>{item.totalFormatted}$</td>
                                <td>{item.currentBalanceFormatted}$</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    )
}

export default Home
