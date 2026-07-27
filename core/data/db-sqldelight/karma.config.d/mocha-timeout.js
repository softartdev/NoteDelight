// SQLDelight web-worker tests perform multiple asynchronous database reopenings.
// Kotlin's browser-test DSL does not expose the Mocha timeout, so configure it
// through Karma for this module only.
config.set({
    client: {
        mocha: {
            timeout: 30000,
        },
    },
});
