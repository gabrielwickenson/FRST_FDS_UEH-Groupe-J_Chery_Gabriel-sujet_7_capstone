const DEFAULT_WIDTH = 800;
const DEFAULT_HEIGHT = 600;

export function img(key, width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT) {
  const safeKey = encodeURIComponent(String(key || "kolabor"));
  const safeWidth = Number.isFinite(width) ? Math.max(1, Math.floor(width)) : DEFAULT_WIDTH;
  const safeHeight = Number.isFinite(height) ? Math.max(1, Math.floor(height)) : DEFAULT_HEIGHT;

  return `https://picsum.photos/seed/${safeKey}/${safeWidth}/${safeHeight}`;
}

export { img as default };
