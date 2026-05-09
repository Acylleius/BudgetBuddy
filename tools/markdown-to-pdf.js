const fs = require('fs');

const [inputPath, outputPath] = process.argv.slice(2);
if (!inputPath || !outputPath) {
  console.error('Usage: node tools/markdown-to-pdf.js input.md output.pdf');
  process.exit(1);
}

const markdown = fs.readFileSync(inputPath, 'utf8')
  .replace(/\r\n/g, '\n')
  .replace(/[^\x09\x0A\x0D\x20-\x7E]/g, '-');

function wrap(line, width = 112) {
  if (line.length <= width) return [line];
  const chunks = [];
  let rest = line;
  while (rest.length > width) {
    let splitAt = rest.lastIndexOf(' ', width);
    if (splitAt < 1) splitAt = width;
    chunks.push(rest.slice(0, splitAt));
    rest = rest.slice(splitAt).trimStart();
  }
  chunks.push(rest);
  return chunks;
}

function escapePdfText(text) {
  return text.replace(/\\/g, '\\\\').replace(/\(/g, '\\(').replace(/\)/g, '\\)');
}

const lines = markdown.split('\n').flatMap(line => wrap(line));
const linesPerPage = 58;
const pages = [];
for (let i = 0; i < lines.length; i += linesPerPage) {
  pages.push(lines.slice(i, i + linesPerPage));
}

const objects = [];
function addObject(body) {
  objects.push(body);
  return objects.length;
}

const catalogId = addObject('<< /Type /Catalog /Pages 2 0 R >>');
const pagesId = addObject('');
const fontId = addObject('<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>');
const pageIds = [];

for (const pageLines of pages) {
  const stream = [
    'BT',
    '/F1 8 Tf',
    '44 792 Td',
    '11 TL',
    ...pageLines.map(line => `(${escapePdfText(line)}) Tj T*`),
    'ET'
  ].join('\n');
  const contentId = addObject(`<< /Length ${Buffer.byteLength(stream)} >>\nstream\n${stream}\nendstream`);
  const pageId = addObject(`<< /Type /Page /Parent ${pagesId} 0 R /MediaBox [0 0 612 842] /Resources << /Font << /F1 ${fontId} 0 R >> >> /Contents ${contentId} 0 R >>`);
  pageIds.push(pageId);
}

objects[pagesId - 1] = `<< /Type /Pages /Kids [${pageIds.map(id => `${id} 0 R`).join(' ')}] /Count ${pageIds.length} >>`;

let pdf = '%PDF-1.4\n';
const offsets = [0];
for (let i = 0; i < objects.length; i++) {
  offsets.push(Buffer.byteLength(pdf));
  pdf += `${i + 1} 0 obj\n${objects[i]}\nendobj\n`;
}
const xrefOffset = Buffer.byteLength(pdf);
pdf += `xref\n0 ${objects.length + 1}\n`;
pdf += '0000000000 65535 f \n';
for (let i = 1; i < offsets.length; i++) {
  pdf += `${String(offsets[i]).padStart(10, '0')} 00000 n \n`;
}
pdf += `trailer\n<< /Size ${objects.length + 1} /Root ${catalogId} 0 R >>\nstartxref\n${xrefOffset}\n%%EOF\n`;

fs.writeFileSync(outputPath, pdf, 'binary');
