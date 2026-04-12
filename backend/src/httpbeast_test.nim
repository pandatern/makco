import httpbeast
import asyncdispatch

proc onRequest(req: Request) {.async.} =
  if req.path == "/":
    req.send(Http200, "OK")
  else:
    req.send(Http404, "Not Found")

when isMainModule:
  runHttpBeast(port = Port(8080))