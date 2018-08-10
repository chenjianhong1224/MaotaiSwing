function getEncrypt(e) {
      function a(e) {
        return n(o(s(e)))
      }
      function o(e) {
        return l(c(d(e), 8 * e.length))
      }
      function n(e) {
        try {
        } catch (t) {
          v = 0
        }
        for (var r, a = v ? '0123456789ABCDEF' : '0123456789abcdef', o = '', n = 0; n < e.length; n++) r = e.charCodeAt(n),
        o += a.charAt(r >>> 4 & 15) + a.charAt(15 & r);
        return o
      }
      function i(e) {
        try {
        } catch (t) {
          b = ''
        }
        for (var r = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/', a = '', o = e.length, n = 0; n < o; n += 3) for (var i = e.charCodeAt(n) << 16 | (n + 1 < o ? e.charCodeAt(n + 1) << 8 : 0) | (n + 2 < o ? e.charCodeAt(n + 2)  : 0), s = 0; s < 4; s++) a += 8 * n + 6 * s > 8 * e.length ? b : r.charAt(i >>> 6 * (3 - s) & 63);
        return a
      }
      function s(e) {
        for (var t, r, a = '', o = - 1; ++o < e.length; ) t = e.charCodeAt(o),
        r = o + 1 < e.length ? e.charCodeAt(o + 1)  : 0,
        55296 <= t && t <= 56319 && 56320 <= r && r <= 57343 && (t = 65536 + ((1023 & t) << 10) + (1023 & r), o++),
        t <= 127 ? a += String.fromCharCode(t)  : t <= 2047 ? a += String.fromCharCode(192 | t >>> 6 & 31, 128 | 63 & t)  : t <= 65535 ? a += String.fromCharCode(224 | t >>> 12 & 15, 128 | t >>> 6 & 63, 128 | 63 & t)  : t <= 2097151 && (a += String.fromCharCode(240 | t >>> 18 & 7, 128 | t >>> 12 & 63, 128 | t >>> 6 & 63, 128 | 63 & t));
        return a
      }
      function d(e) {
        for (var t = Array(e.length >> 2), r = 0; r < t.length; r++) t[r] = 0;
        for (var r = 0; r < 8 * e.length; r += 8) t[r >> 5] |= (255 & e.charCodeAt(r / 8)) << r % 32;
        return t
      }
      function l(e) {
        for (var t = '', r = 0; r < 32 * e.length; r += 8) t += String.fromCharCode(e[r >> 5] >>> r % 32 & 255);
        return t
      }
      function c(e, t) {
        e[t >> 5] |= 128 << t % 32,
        e[(t + 64 >>> 9 << 4) + 14] = t;
        for (var r = 1732584193, a = - 271733879, o = - 1732584194, n = 271733878, i = 0; i < e.length; i += 16) {
          var s = r,
          d = a,
          l = o,
          c = n;
          r = m(r, a, o, n, e[i + 0], 7, - 680876936),
          n = m(n, r, a, o, e[i + 1], 12, - 389564586),
          o = m(o, n, r, a, e[i + 2], 17, 606105819),
          a = m(a, o, n, r, e[i + 3], 22, - 1044525330),
          r = m(r, a, o, n, e[i + 4], 7, - 176418897),
          n = m(n, r, a, o, e[i + 5], 12, 1200080426),
          o = m(o, n, r, a, e[i + 6], 17, - 1473231341),
          a = m(a, o, n, r, e[i + 7], 22, - 45705983),
          r = m(r, a, o, n, e[i + 8], 7, 1770035416),
          n = m(n, r, a, o, e[i + 9], 12, - 1958414417),
          o = m(o, n, r, a, e[i + 10], 17, - 42063),
          a = m(a, o, n, r, e[i + 11], 22, - 1990404162),
          r = m(r, a, o, n, e[i + 12], 7, 1804603682),
          n = m(n, r, a, o, e[i + 13], 12, - 40341101),
          o = m(o, n, r, a, e[i + 14], 17, - 1502002290),
          a = m(a, o, n, r, e[i + 15], 22, 1236535329),
          r = h(r, a, o, n, e[i + 1], 5, - 165796510),
          n = h(n, r, a, o, e[i + 6], 9, - 1069501632),
          o = h(o, n, r, a, e[i + 11], 14, 643717713),
          a = h(a, o, n, r, e[i + 0], 20, - 373897302),
          r = h(r, a, o, n, e[i + 5], 5, - 701558691),
          n = h(n, r, a, o, e[i + 10], 9, 38016083),
          o = h(o, n, r, a, e[i + 15], 14, - 660478335),
          a = h(a, o, n, r, e[i + 4], 20, - 405537848),
          r = h(r, a, o, n, e[i + 9], 5, 568446438),
          n = h(n, r, a, o, e[i + 14], 9, - 1019803690),
          o = h(o, n, r, a, e[i + 3], 14, - 187363961),
          a = h(a, o, n, r, e[i + 8], 20, 1163531501),
          r = h(r, a, o, n, e[i + 13], 5, - 1444681467),
          n = h(n, r, a, o, e[i + 2], 9, - 51403784),
          o = h(o, n, r, a, e[i + 7], 14, 1735328473),
          a = h(a, o, n, r, e[i + 12], 20, - 1926607734),
          r = f(r, a, o, n, e[i + 5], 4, - 378558),
          n = f(n, r, a, o, e[i + 8], 11, - 2022574463),
          o = f(o, n, r, a, e[i + 11], 16, 1839030562),
          a = f(a, o, n, r, e[i + 14], 23, - 35309556),
          r = f(r, a, o, n, e[i + 1], 4, - 1530992060),
          n = f(n, r, a, o, e[i + 4], 11, 1272893353),
          o = f(o, n, r, a, e[i + 7], 16, - 155497632),
          a = f(a, o, n, r, e[i + 10], 23, - 1094730640),
          r = f(r, a, o, n, e[i + 13], 4, 681279174),
          n = f(n, r, a, o, e[i + 0], 11, - 358537222),
          o = f(o, n, r, a, e[i + 3], 16, - 722521979),
          a = f(a, o, n, r, e[i + 6], 23, 76029189),
          r = f(r, a, o, n, e[i + 9], 4, - 640364487),
          n = f(n, r, a, o, e[i + 12], 11, - 421815835),
          o = f(o, n, r, a, e[i + 15], 16, 530742520),
          a = f(a, o, n, r, e[i + 2], 23, - 995338651),
          r = p(r, a, o, n, e[i + 0], 6, - 198630844),
          n = p(n, r, a, o, e[i + 7], 10, 1126891415),
          o = p(o, n, r, a, e[i + 14], 15, - 1416354905),
          a = p(a, o, n, r, e[i + 5], 21, - 57434055),
          r = p(r, a, o, n, e[i + 12], 6, 1700485571),
          n = p(n, r, a, o, e[i + 3], 10, - 1894986606),
          o = p(o, n, r, a, e[i + 10], 15, - 1051523),
          a = p(a, o, n, r, e[i + 1], 21, - 2054922799),
          r = p(r, a, o, n, e[i + 8], 6, 1873313359),
          n = p(n, r, a, o, e[i + 15], 10, - 30611744),
          o = p(o, n, r, a, e[i + 6], 15, - 1560198380),
          a = p(a, o, n, r, e[i + 13], 21, 1309151649),
          r = p(r, a, o, n, e[i + 4], 6, - 145523070),
          n = p(n, r, a, o, e[i + 11], 10, - 1120210379),
          o = p(o, n, r, a, e[i + 2], 15, 718787259),
          a = p(a, o, n, r, e[i + 9], 21, - 343485551),
          r = y(r, s),
          a = y(a, d),
          o = y(o, l),
          n = y(n, c)
        }
        return Array(r, a, o, n)
      }
      function u(e, t, r, a, o, n) {
        return y(g(y(y(t, e), y(a, n)), o), r)
      }
      function m(e, t, r, a, o, n, i) {
        return u(t & r | ~t & a, e, t, o, n, i)
      }
      function h(e, t, r, a, o, n, i) {
        return u(t & a | r & ~a, e, t, o, n, i)
      }
      function f(e, t, r, a, o, n, i) {
        return u(t ^ r ^ a, e, t, o, n, i)
      }
      function p(e, t, r, a, o, n, i) {
        return u(r ^ (t | ~a), e, t, o, n, i)
      }
      function y(e, t) {
        var r = (65535 & e) + (65535 & t),
        a = (e >> 16) + (t >> 16) + (r >> 16);
        return a << 16 | 65535 & r
      }
      function g(e, t) {
        return e << t | e >>> 32 - t
      }
      var v = 0,
      b = '',
      w = {
        hex_md5: a,
        rstr2b64: i
      };
      return i(e);
    }