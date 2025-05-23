module.exports = {
    // Output to the Spring Boot static resources directory
    outputDir: '../../../build/resources/main/static',

    // Don't add a hash to filenames for easier integration with Spring Boot
    filenameHashing: false,

    // Don't use runtime compiler to reduce bundle size
    runtimeCompiler: false,

    // Don't generate source maps in production
    productionSourceMap: false,

    // Configure webpack
    configureWebpack: {
        // Set the entry point
        entry: {
            app: './src/main.js'
        },
        // Configure output
        output: {
            filename: 'js/[name].js',
            chunkFilename: 'js/[name].js'
        }
    },

    // Configure CSS
    css: {
        extract: {
            filename: 'css/[name].css',
            chunkFilename: 'css/[name].css'
        }
    }
}