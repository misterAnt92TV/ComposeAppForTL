const { execFileSync } = require("node:child_process");
const fs = require("node:fs");
const path = require("node:path");
const sharp = require("sharp");

const repoRoot = path.resolve(__dirname, "..", "..");
const sourceFullIcon = path.join(__dirname, "tlincompose-app-icon.svg");
const sourceForegroundIcon = path.join(__dirname, "tlincompose-app-icon-foreground.svg");
const outputDir = path.join(__dirname, "generated");
const desktopIconsDir = path.join(repoRoot, "composeApp", "src", "desktopMain", "resources", "icons");
const androidResDir = path.join(repoRoot, "composeApp", "src", "androidMain", "res");
const macIconsetDir = path.join(outputDir, "tlincompose.iconset");

function ensureDir(dirPath) {
  fs.mkdirSync(dirPath, { recursive: true });
}

function tryBuildIcns(sourcePng, outputFile) {
  const pythonScript = [
    "from PIL import Image",
    "import sys",
    "img = Image.open(sys.argv[1])",
    "img.save(sys.argv[2], sizes=[(16,16),(32,32),(64,64),(128,128),(256,256),(512,512),(1024,1024)])",
  ].join(";");

  const candidates = [process.env.PYTHON_BIN, "python3"].filter(Boolean);

  for (const candidate of candidates) {
    try {
      execFileSync(candidate, ["-c", pythonScript, sourcePng, outputFile], { stdio: "ignore" });
      return true;
    } catch (error) {
      // Try the next Python candidate until one can save the ICNS file.
    }
  }

  return false;
}

async function renderPng(inputFile, outputFile, size) {
  await sharp(inputFile)
    .resize(size, size)
    .png({ compressionLevel: 9 })
    .toFile(outputFile);
}

async function buildIco(sourceFile, outputFile, sizes) {
  const rendered = await Promise.all(
    sizes.map(async (size) => {
      const buffer = await sharp(sourceFile)
        .resize(size, size)
        .png({ compressionLevel: 9 })
        .toBuffer();
      return { size, buffer };
    }),
  );

  const header = Buffer.alloc(6);
  header.writeUInt16LE(0, 0);
  header.writeUInt16LE(1, 2);
  header.writeUInt16LE(rendered.length, 4);

  const directory = Buffer.alloc(rendered.length * 16);
  let offset = header.length + directory.length;

  rendered.forEach(({ size, buffer }, index) => {
    const base = index * 16;
    directory.writeUInt8(size === 256 ? 0 : size, base);
    directory.writeUInt8(size === 256 ? 0 : size, base + 1);
    directory.writeUInt8(0, base + 2);
    directory.writeUInt8(0, base + 3);
    directory.writeUInt16LE(1, base + 4);
    directory.writeUInt16LE(32, base + 6);
    directory.writeUInt32LE(buffer.length, base + 8);
    directory.writeUInt32LE(offset, base + 12);
    offset += buffer.length;
  });

  const icoFile = Buffer.concat([header, directory, ...rendered.map(({ buffer }) => buffer)]);
  fs.writeFileSync(outputFile, icoFile);
}

async function main() {
  ensureDir(outputDir);
  ensureDir(desktopIconsDir);
  ensureDir(path.join(androidResDir, "drawable"));

  await renderPng(sourceFullIcon, path.join(outputDir, "tlincompose-app-icon-1024.png"), 1024);
  await renderPng(sourceForegroundIcon, path.join(outputDir, "tlincompose-app-icon-foreground-432.png"), 432);

  const androidMipmaps = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
  };

  for (const [dirName, size] of Object.entries(androidMipmaps)) {
    const dirPath = path.join(androidResDir, dirName);
    ensureDir(dirPath);
    await renderPng(sourceFullIcon, path.join(dirPath, "ic_launcher.png"), size);
    await renderPng(sourceFullIcon, path.join(dirPath, "ic_launcher_round.png"), size);
  }

  await renderPng(sourceForegroundIcon, path.join(androidResDir, "drawable", "ic_launcher_foreground.png"), 432);

  await renderPng(sourceFullIcon, path.join(desktopIconsDir, "tlincompose-window.png"), 256);
  await renderPng(sourceFullIcon, path.join(desktopIconsDir, "tlincompose-linux.png"), 512);

  ensureDir(macIconsetDir);
  const macIconsetFiles = {
    "icon_16x16.png": 16,
    "icon_16x16@2x.png": 32,
    "icon_32x32.png": 32,
    "icon_32x32@2x.png": 64,
    "icon_128x128.png": 128,
    "icon_128x128@2x.png": 256,
    "icon_256x256.png": 256,
    "icon_256x256@2x.png": 512,
    "icon_512x512.png": 512,
    "icon_512x512@2x.png": 1024,
  };

  for (const [fileName, size] of Object.entries(macIconsetFiles)) {
    await renderPng(sourceFullIcon, path.join(macIconsetDir, fileName), size);
  }

  await buildIco(sourceFullIcon, path.join(desktopIconsDir, "tlincompose.ico"), [16, 24, 32, 48, 64, 128, 256]);
  const icnsCreated = tryBuildIcns(
    path.join(outputDir, "tlincompose-app-icon-1024.png"),
    path.join(desktopIconsDir, "tlincompose.icns"),
  );

  console.log("Generated icon assets in:");
  console.log(`- ${desktopIconsDir}`);
  console.log(`- ${androidResDir}`);
  console.log(`- ${outputDir}`);
  console.log(`- macOS ICNS ${icnsCreated ? "generated" : "skipped (python3 + Pillow not available)"}`);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
