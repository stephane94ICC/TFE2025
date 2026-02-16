const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  outputDir: '../src/main/resources/static',  // Spécifie le dossier 'static' de Spring Boot pour le build
  transpileDependencies: true
});
