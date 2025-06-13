// composeApp/webpack.config.d/sqljs.js
const CopyWebpackPlugin = require('copy-webpack-plugin');

config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
    },
};

config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: require.resolve('sql.js/dist/sql-wasm.wasm'),
                to: 'sql-wasm.wasm'
            }
        ]
    })
);