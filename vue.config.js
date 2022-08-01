const path = require('path')

module.exports = {
    outputDir: 'src/main/resources/static',
    pages: {
        index: {
            entry: 'src/main/js/main.js',
            template: 'src/main/resources/template/index.html'
        }
    },
    configureWebpack: {
        resolve: {
            alias: {
                '@': path.resolve(__dirname, 'src/main/js')
            }
        }
    },
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080'
            },
            '/login': {
                target: 'http://localhost:8080'
            },
        }
    },
    transpileDependencies: [
        'vuetify'
    ]
}
