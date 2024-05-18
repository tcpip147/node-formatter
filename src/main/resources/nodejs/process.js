const vscodeCssLanguageService = require('vscode-css-languageservice');
const cssLanguageService = vscodeCssLanguageService.getCSSLanguageService();

let buffer = Buffer.alloc(0);
process.stdin.on('data', function(data) {
    buffer = Buffer.concat([buffer, data]);
    let nullByteIndex;
    while ((nullByteIndex = buffer.indexOf(0)) !== -1) {
      const request = JSON.parse(buffer.slice(0, nullByteIndex));
      const document = vscodeCssLanguageService.TextDocument.create('', 'css', 1, request.data.text);
      const stylesheet = cssLanguageService.parseStylesheet(document);
      const position = vscodeCssLanguageService.Position.create(request.data.line, request.data.column);
      const completion = cssLanguageService.doComplete(document, position, stylesheet);
      const completionList = [];
      for (let i = 0; i < completion.items.length; i++) {
        const item = completion.items[i];
        completionList.push(item.textEdit.newText);
      }
      request.data = {
        completion: completionList
      };
      sendResponse(request);
      buffer = buffer.slice(nullByteIndex + 1);
    }
});

function sendResponse(response) {
  process.stdout.write(JSON.stringify(response));
  process.stdout.write("\x00");
}