import React from 'react'
import {t} from '../utils/TranslationUtils'

function Home() {
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
                    {t('home.description', {documentationLink})}
                </p>
            </div>
        </div>
    )
}

export default Home
