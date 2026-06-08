export function createSocketClient(url: string) {
  return new WebSocket(url);
}
