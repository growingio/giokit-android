function randomString(t) {
        var e = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz".split("");
        t || (t = Math.floor(Math.random() * e.length));
        for (var n = "", i = 0; i < t; i++) n += e[Math.floor(Math.random() * e.length)];
        return n
    }! function (t) {
        var e = function () {
            var t = {},
                e = function (e, n, i) {
                    if (t[e]) {
                        var r = t[e],
                            a = r.length - 1;
                        for (a; a >= 0; a -= 1) r[a].call(i || this, n || [])
                    }
                },
                n = function (e, n) {
                    return t[e] || (t[e] = []), t[e].push(n), [e, n]
                },
                i = function (e, n) {
                    var i = e[0],
                        r = t[i].length - 1;
                    if (t[i])
                        for (r; r >= 0; r -= 1) t[i][r] === e[1] && (t[i].splice(t[i][r], 1), n && delete t[i])
                };
            return {
                publish: e,
                subscribe: n,
                unsubscribe: i
            }
        }();
        t.MQ = e, t.CircleEvents = e
    }(window),
    function () {
        var t, e, n, i, r, a, s, o, h, l, u, c, d, p, f, g, m, v, y, w, x, C, b, _, E, S, O, A, k = function (t, e) {
                return function () {
                    return t.apply(e, arguments)
                }
            },
            M = [].indexOf || function (t) {
                for (var e = 0, n = this.length; e < n; e++)
                    if (e in this && this[e] === t) return e;
                return -1
            },
            T = {}.hasOwnProperty;
        n = function () {
            function t() {}
            return t.isSame = function (e, n) {
                var i, r, a, s, o, h;
                if (t._getFrame(e) !== t._getFrame(n)) return !1;
                if (o = e.split("/|\\.|#"), h = n.split("/|\\.|#"), o.length !== h.length) return !1;
                for (a = s = 0, i = o.length; s < i; a = ++s)
                    if (r = o[a], "*" !== o[a] && "*" !== h[a] && o[a] !== h[a]) return !1;
                return !0
            }, t._getFrame = function (t) {
                var e, n, i, r, a;
                for (a = "", e = t.split(""), r = 0, i = e.length; r < i; r++) n = e[r], "/" !== n && "#" !== n && "." !== n || (a += n);
                return a
            }, t.isSameWithRegular = function (e, n) {
                var i, r;
                try {
                    i = new RegExp(e.replace(/\*/g, ".*"))
                } catch (a) {
                    i = new RegExp("")
                }
                try {
                    r = new RegExp(n.replace(/\*/g, ".*"))
                } catch (a) {
                    r = new RegExp("")
                }
                return t.isSame(e, n) || i.test(n) || r.test(e)
            }, t.isSameWithSkeleton = function (t, e) {
                var n;
                return t === e || !!e && (n = e.split("/").map(function (t) {
                    return t.replace(/[\.|\#].*$/i, "")
                }), t === n.join("/"))
            }, t
        }(), p = function () {
            function t(t) {
                var e, n;
                t && (this.node = t, this.name = t.tagName.toLowerCase(), t.hasAttribute("id") && null === t.getAttribute("id").match(/^[0-9]/) && (this.id = t.getAttribute("id")), t.hasAttribute("href") && (this.href = t.getAttribute("href")), t.hasAttribute("src") && (this.href = t.getAttribute("src")), "input" === this.name && t.hasAttribute("name") && t.getAttribute("name") ? this.klass = [t.getAttribute("name")] : (e = null != (n = t.getAttribute("class")) ? n.replace(/(^| )(clear|clearfix|active|hover|enabled|hidden|display|focus|disabled|growing-|ng-)[^\. ]*/g, "").trim() : void 0, (null != e ? e.length : void 0) > 0 && (this.klass = e.split(/\s+/).sort())))
            }
            return t.prototype.init = function (t) {
                if (t) return this.klass = t.klass, this.name = t.name, this.node = t.node, this.id = t.id, this.href = t.href, m.clone(this)
            }, t.prototype.render = function (t) {
                var e, n;
                return e = {
                    name: !0,
                    id: !0,
                    klass: !0
                }, "undefined" != typeof t && (0 !== t.indexOf(this.name) && (e.name = !1), null != this.id && t.indexOf("#" + this.id) === -1 && (e.id = !1), null != this.klass && t.indexOf(".") === -1 && (e.klass = !1)), n = window.JST["dom_node.jst"]({
                    node: this,
                    included: e
                })
            }, t.prototype.path = function () {
                var t, e, n, i, r;
                if (n = "/" + this.name, null != this.id && (n += "#" + this.id), null != this.klass)
                    for (r = this.klass, i = 0, e = r.length; i < e; i++) t = r[i], n += "." + t;
                return n
            }, t.prototype.similarPath = function (t) {
                var e;
                return null == t && (t = !1), e = "/" + this.name, t ? (null != this.id && (e += "#" + this.id), null != this.klass && (e += "*")) : null == this.id && null == this.klass || (e += "*"), e
            }, t
        }(), i = function () {
            function t(t) {
                this.toQuery = k(this.toQuery, this), this.grXpath = t
            }
            return t.prototype.findElements = function () {
                var t, e, n;
                try {
                    n = this.toQuery(), t = document.querySelectorAll(n), 0 === t.length && (n.match(/input\[name=/) ? (n = n.replace(/input\[name=\'(.+)\']/, "input.$1"), t = document.querySelectorAll(n)) : n.match(/input#.*\[name=/) && (n = n.replace(/input#.*\[name=\'(.+)\']/, "input.$1"), t = document.querySelectorAll(n)))
                } catch (i) {
                    e = i, console.warn(e), t = []
                }
                return t
            }, t.prototype.toQuery = function () {
                var t;
                return t = this.grXpath.replace(/^\//, "").split("/").map(function (t) {
                    return function (e) {
                        return t._parseNode(e)
                    }
                }(this)), "body > " + t.join(" > ")
            }, t.prototype._parseNode = function (t) {
                var e, n, i, r, a, s, o;
                if (0 === t.indexOf("*")) return "*";
                if (r = t.split(/\.|\#/), s = [""], r.length > 1) {
                    for (o = 0, i = t.length; o < i; o++) e = t[o], [".", "#"].indexOf(e) !== -1 && s.push(e);
                    return n = r.map(function (t, e) {
                        return 0 === e ? t.replace("*", "") : t.match(/[^a-zA-Z0-9\-\_]/) ? null : isNaN(+t[0]) ? t : null
                    }), n.map(function (t, e) {
                        if (t) return "input" === r[0] && "." === s[e] && 1 === s.filter(function (t) {
                            return "." === t
                        }).length ? "[name='" + t + "']" : s[e] + t
                    }).join("")
                }
                return a = t.indexOf("*"), a === -1 ? t : t.substring(0, a)
            }, t
        }(), E = /^(?:body|html)$/i, g = /^(((([^:\/#\?]+:)?(?:(\/\/)((?:(([^:@\/#\?]+)(?:\:([^:@\/#\?]+))?)@)?(([^:\/#\?\]\[]+|\[[^\/\]@#?]+\])(?:\:([0-9]+))?))?)?)?((\/?(?:[^\/\?#]+\/+)*)([^\?#]*)))?(\?[^#]+)?)(#.*)?/, S = ["I", "SPAN", "EM", "svg"], O = ["A", "BUTTON"], b = ["TR", "LI", "DL"], y = {
            "column-count": 1,
            columns: 1,
            "font-weight": 1,
            "line-height": 1,
            opacity: 1,
            "z-index": 1,
            zoom: 1
        }, v = function (t) {
            return t.replace(/-+(.)?/g, function (t, e) {
                return e ? e.toUpperCase() : ""
            })
        }, w = function (t) {
            return t.replace(/::/g, "/").replace(/([A-Z]+)([A-Z][a-z])/g, "$1_$2").replace(/([a-z\d])([A-Z])/g, "$1_$2").replace(/_/g, "-").toLowerCase()
        }, C = Array.isArray || function (t) {
            return t instanceof Array
        }, null == Element.prototype.remove && (Element.prototype.remove = function () {
            if (this.parentNode) return this.parentNode.removeChild(this)
        }), _ = function (t, e) {
            return "number" != typeof e || y[w(t)] ? e : e + "px"
        }, m = function () {
            function t() {}
            return t.bind = function (t, e, n, i) {
                var r;
                if (null == i && (i = !1), null != t) return null != document.addEventListener ? t.addEventListener(e, n, i) : null != document.attachEvent ? (r = e + n, t["e" + r] = n, t[r] = function () {
                    var e;
                    return e = window.event, e.currentTarget = t, e.target = e.srcElement, t["e" + r].call(t, e)
                }, t.attachEvent("on" + e, t[r])) : t["on" + e] = n, !0
            }, t.unbind = function (t, e, n, i) {
                var r;
                if (null != t) return null != document.removeEventListener ? t.removeEventListener(e, n, i) : null != document.detachEvent ? (r = e + n, t.detachEvent("on" + e, t[r]), t[r] = null, t["e" + r] = null) : t["on" + e] = null, !0
            }, t.bindOn = function (e, n, i, r, a) {
                var s, o, h, l;
                switch (null == a && (a = !1), typeof i) {
                case "string":
                    for (l = e.querySelectorAll(i), h = 0, o = l.length; h < o; h++) s = l[h], t.bindOnce(s, n, r, a);
                    break;
                case "function":
                    a = null !== r && r, r = i, t.bindOnce(e, n, r, a)
                }
                return !0
            }, t.bindOnce = function (e, n, i, r) {
                return null == r && (r = !1), t.unbind(e, n, i, r), t.bind(e, n, i, r)
            }, t.isLeaf = function (t) {
                var e, n, i, r;
                if (t.hasChildNodes() && "svg" !== t.tagName)
                    for (r = t.childNodes, i = 0, n = r.length; i < n; i++)
                        if (e = r[i], 1 === e.nodeType) return !1;
                return !0
            }, t.isParentOfLeaf = function (e) {
                var n, i, r, a;
                if (!e.hasChildNodes()) return !1;
                if ("svg" === e.tagName) return !1;
                for (a = e.childNodes, r = 0, i = a.length; r < i; r++)
                    if (n = a[r], !t.isLeaf(n)) return !1;
                return !0
            }, t.lessThanSomeLevelDepth = function (e, n, i) {
                var r, a, s, o, h;
                if (null == i && (i = 1), r = e.childNodes, r.length > 0) {
                    if (i > n) return !1;
                    for (h = e.childNodes, o = 0, a = h.length; o < a; o++)
                        if (s = h[o], s.nodeType === Node.ELEMENT_NODE && !t.lessThanSomeLevelDepth(s, n, i + 1)) return !1
                }
                return i <= n
            }, t.parentOfLeafText = function (t) {
                var e, n, i, r, a, s;
                if (i = "", !t.hasChildNodes()) return "";
                for (s = t.childNodes, a = 0, r = s.length; a < r; a++) e = s[a], 3 === e.nodeType && (null != e.textContent ? n = this.trim(e.textContent) : null != e.data && (n = this.trim(e.data)), n.length > 0 && (i += n + " "));
                return i = i.replace(/[\n \t]+/g, " ").trim(), i.length > 0 && i.length < 50 ? i : void 0
            }, t.tree = function (t) {
                var e, n;
                for (n = [], e = new p(t);
                    "body" !== e.name && "html" !== e.name;) n.unshift(e), e = new p(e.node.parentNode);
                return n
            }, t.path = function (t) {
                var e, n, i, r, a;
                for (e = "", a = this.tree(t), i = 0, n = a.length; i < n; i++) r = a[i], e += r.path();
                return e
            }, t.nodePath = function (t) {
                var e, n, i, r, a;
                for (e = "", a = this.tree(t), i = 0, n = a.length; i < n; i++) r = a[i], e += r.path();
                return e
            }, t.getGrObj = function (t) {
                var e, n;
                if (n = t, this.hasAttr(t, "data-growing-info") && (e = t.getAttribute("data-growing-info")), e) return e;
                for (; n && "BODY" !== n.tagName && !this.isContainer(n) && (["TR"].indexOf(n.tagName) === -1 || !this.hasAttr(n, "data-growing-info"));) n = n.parentNode;
                return this.hasAttr(n, "data-growing-info") ? n.getAttribute("data-growing-info") : void 0
            }, t.index = function (t) {
                var e, n;
                return this.hasAttr(t, "data-growing-idx") ? t.getAttribute("data-growing-idx") : (e = this.calculateExtendsIdx(t), n = this.nodePath(t), n && !e && this.isInList(n) && (e = this._calculateListIdx(t)), e)
            }, t.calculateExtendsIdx = function (t) {
                var e;
                for (e = t; e && "BODY" !== e.tagName && !this.hasAttr(e, "data-growing-idx");) e = e.parentNode;
                if (e) return e.getAttribute("data-growing-idx")
            }, t._calculateListIdx = function (t) {
                var e, n, i, r, a, s, o;
                for (r = t; r && "BODY" !== r.tagName && this.indexOf(b, r.tagName) === -1;) r = r.parentNode;
                if (r)
                    for (a = r.parentNode, e = 1, o = a.childNodes, s = 0, n = o.length; s < n; s++)
                        if (i = o[s], i.tagName === r.tagName) {
                            if (this.hasAttr(i, "data-growing-idx") && (e = parseInt(i.getAttribute("data-growing-idx"))), i === r) return e;
                            e += 1
                        }
            }, t.isInList = function (t) {
                return t.split(/\.|\/|\#/).some(function (t) {
                    return b.some(function (e) {
                        return e.toLowerCase() === t.toLowerCase()
                    })
                })
            }, t.href = function (t) {
                var e, n, i, r;
                return r = t.tagName, "IMG" === r ? (null != (i = t.src) ? i.length : void 0) > 0 && t.src.indexOf("data:image") === -1 && (n = t.src) : this.hasAttr(t, "href") && (e = t.getAttribute("href"), e && 0 !== e.indexOf("javascript") && (n = this.normalizePath(e.slice(0, 320)))), n
            }, t.siblingList = function (t) {
                var e, n;
                for (n = t; n && "BODY" !== n.tagName && ["TR", "LI", "DL"].indexOf(n.tagName) === -1;) n = n.parentNode;
                if (n) return function () {
                    var t, i, r, a;
                    for (r = n.parentNode.childNodes, a = [], i = 0, t = r.length; i < t; i++) e = r[i], e.offsetParent && a.push(e);
                    return a
                }()
            }, t.similarPath = function (t) {
                var e, n, i;
                for (i = "", n = 0, e = new p(t);
                    "body" !== e.name;) {
                    if (null != e.id) {
                        if (n) return i = "" + e.similarPath(!0) + i, this.path(e.node.parentNode) + i;
                        n = !0
                    }
                    i = "" + e.similarPath() + i, e = new p(e.node.parentNode)
                }
                return i
            }, t.hasClass = function (t, e) {
                return null != t && t.classList.contains(e)
            }, t.addClass = function (t, e) {
                return null != t && t.classList.add(e)
            }, t.removeClass = function (t, e) {
                return null != t && t.classList.remove(e)
            }, t.toggleClass = function (t, e) {
                return this.hasClass(t, e) ? this.removeClass(t, e) : this.addClass(t, e)
            }, t.offset = function (t) {
                var e, n, i, r, a;
                if (t) return r = t.getBoundingClientRect(), r.width || r.height || t.getClientRects().length ? (e = t.ownerDocument, a = t === t.window ? t : 9 === t.nodeType ? t.defaultView : window, n = e.documentElement, i = {
                    top: r.top + a.pageYOffset - n.clientTop,
                    left: r.left + a.pageXOffset - n.clientLeft
                }) : void 0
            }, t.position = function (e) {
                var n, i, r, a;
                return r = t.offsetParent(e), (i = t.offset(e)) ? (a = E.test(r.nodeName) ? {
                    top: 0,
                    left: 0
                } : t.offset(r), a.top += parseFloat(t.css(r, "border-top-width") || 0), a.left += parseFloat(t.css(r, "border-left-width") || 0), n = {
                    top: i.top - a.top,
                    left: i.left - a.left
                }) : null
            }, t.offsetParent = function (e) {
                var n;
                for (n = e.offsetParent || document.body; n && !E.test(n.nodeName) && "static" === t.css(n, "position");) n = n.offsetParent;
                return n
            }, t.width = function (t) {
                var e;
                if (t) return t === t.window ? t.innerWidth : 9 === t.nodeType ? t.documentElement.scrollWidth : (e = t.getBoundingClientRect(), Math.round(e.width))
            }, t.stringCircleEventIfy = function (t) {
                var e;
                return e = this.deepClone(t), JSON.stringify(e)
            }, t.include = function (t, e) {
                var n;
                return n = M.call(t, e) >= 0
            }, t.deepClone = function (t) {
                var e, n, i, r, a, s, o;
                if (s = typeof t, this.isDom(t)) return {};
                if (!t) return null;
                if (C(t)) r = [];
                else {
                    if ("object" !== s) return t;
                    r = {}
                } if ("array" === s) {
                    for (a = 0, i = t.length; a < i; a++) e = t[a], r.push(this.deepClone(e));
                    return r
                }
                if ("object" === s) {
                    for (n in t) o = t[n], r[n] = this.deepClone(o);
                    return r
                }
            }, t.isDom = function (t) {
                return t && "string" == typeof t.nodeName && "object" == typeof t
            }, t.height = function (t) {
                var e;
                if (t) return t === t.window ? t.innerHeight : 9 === t.nodeType ? t.documentElement.scrollHeight : (e = t.getBoundingClientRect(), Math.round(e.height))
            }, t.isHidden = function (t) {
                return "none" === t.style.display
            }, t.closest = function (t, e) {
                for (; t;) {
                    if (e(t)) return t;
                    t = t.parentNode
                }
                return null
            }, t.getAccurateElementsByXpath = function (t, e, r, a, s) {
                var o, h, l, u, c, d, p;
                if (p = [], u = new i(t), l = u.findElements(), 0 === l.length) return p;
                for (o = Array.prototype.slice.call(l), d = 0, c = o.length; d < c; d++) h = o[d], n.isSame(t, this.nodePath(h)) && p.push(h);
                return p
            }, t.getClickElementByTag = function (e) {
                var n, i;
                return i = t.getElementsByXpath(e.filter.xpath, e.filter.content, e.filter.index, e.filter.href), n = e.attrs.domIndex || 0, i[n] || i[0]
            }, t.getElementsBySkeletonXpath = function (t, e, n, i, r) {
                var a, s, o, h, l;
                if (null == r && (r = !1), h = [], t)
                    for (o = t.split(","), s = 0, a = o.length; s < a; s++) l = o[s], h = h.concat(this._queryXpath(l, e, n, i, r, "skeleton"));
                return this.uniq(h)
            }, t.getElementsByXpath = function (t, e, n, i, r) {
                var a, s, o, h, l;
                if (null == r && (r = !1), h = [], t)
                    for (o = t.split(","), s = 0, a = o.length; s < a; s++) l = o[s], h = h.concat(this._queryXpath(l, e, n, i, r));
                return this.uniq(h)
            }, t._queryXpath = function (e, r, a, s, o, h) {
                var l, u, c, d;
                return null == h && (h = "full"), d = new i(e), c = d.findElements(), l = Array.prototype.slice.call(c), l = l.filter(function (i) {
                        return function (a) {
                            var l, u, c, d, p, f, g;
                            if (d = !0, s = i.filterProtocol(s), r || s)
                                if (o) r && (u = t.content(a), u.length > 0 && u === r || (d = !1)), s && (p = a.tagName.toLowerCase(), "img" === p ? d = d && a.src && a.src.indexOf("data:image") === -1 && i.filterProtocol(a.src) === s : (f = a.hasAttribute("href") && i.filterProtocol(t.normalizePath(a.getAttribute("href"))), d = d && f && f === s));
                                else {
                                    if (r) {
                                        try {
                                            l = new RegExp(r.replace(/\*/g, ".*"))
                                        } catch (m) {
                                            l = new RegExp("")
                                        }
                                        u = t.content(a), (null != u ? u.length : void 0) > 0 && (u.indexOf(r) !== -1 || l.test(u)) || (d = !1)
                                    }
                                    if (s) {
                                        try {
                                            c = new RegExp(s.replace(/\*/g, ".*"))
                                        } catch (m) {
                                            c = new RegExp("")
                                        }
                                        p = a.tagName.toLowerCase(), "img" === p ? (g = i.filterProtocol(a.src), d = d && g && g.indexOf("data:image") === -1 && (g === s || c.test(g))) : (f = a.hasAttribute("href") && i.filterProtocol(t.normalizePath(a.getAttribute("href"))), d = d && f && (f === s || c.test(f)))
                                    }
                                }
                            return "skeleton" === h ? d && n.isSameWithSkeleton(e, i.nodePath(a)) : d && n.isSameWithRegular(e, i.nodePath(a))
                        }
                    }(this)), a && 0 !== parseInt(a) && (l = function () {
                        var e, n, i;
                        for (i = [], n = 0, e = l.length; n < e; n++) u = l[n], parseInt(t.index(u)) === parseInt(a) && i.push(u);
                        return i
                    }()),
                    function () {
                        var t, e, n;
                        for (n = [], e = 0, t = l.length; e < t; e++) u = l[e], (null != u ? u.offsetParent : void 0) && n.push(u);
                        return n
                    }()
            }, t._xpathRemoveEmptyClassOrId = function (t) {
                var e;
                return e = t.replace(/(\S)\#\./g, "$1.").replace(/(\S)\#\s/g, "$1 "), e.replace(/\.(\s)/g, "$1").replace(/(\S)\.\s/, "$1 "), e
            }, t.content = function (t) {
                var e, n;
                return n = t.tagName.toLowerCase(), this.hasAttr(t, "data-growing-title") && t.getAttribute("data-growing-title").length > 0 ? e = t.getAttribute("data-growing-title") : this.hasAttr(t, "title") && t.getAttribute("title").length > 0 ? e = t.getAttribute("title") : this.isLeaf(t) ? e = this.calculateLeafContent(t) : this.indexOf(["a", "button"], n) !== -1 ? e = this.containerElemContent(t) : this.isParentOfLeaf(t) && (e = this.parentOfLeafText(t)), e = e || ""
            }, t.isContainer = function (t) {
                return this.hasAttr(t, "data-growing-container")
            }, t.onlyContainsChildren = function (t, e) {
                var n, i, r, a;
                if (0 === !t.children.length) return !1;
                for (a = t.children, r = 0, i = a.length; r < i; r++)
                    if (n = a[r], this.indexOf(e, n.tagName) === -1) return !1;
                return !0
            }, t.calculateLeafContent = function (t) {
                var e, n, i, r, a, s, o, h, l, u;
                if (l = t.tagName.toLowerCase(), i = this.href(t), "img" === l) t.alt ? e = t.alt : i && (a = i.split("?")[0], r = a.split("/"), r.length > 0 && (e = r[r.length - 1]));
                else if ("input" === l && this.indexOf(["button", "submit"], t.type) !== -1) e = t.value;
                else if ("svg" === l)
                    for (h = t.childNodes, o = 0, s = h.length; o < s; o++) n = h[o], 1 === n.nodeType && "use" === n.tagName && this.hasAttr(n, "xlink:href") && (e = n.getAttribute("xlink:href"));
                else u = "", null != t.textContent ? u = t.textContent.replace(/[\n \t]+/g, " ").trim() : null != t.innerText && (u = t.innerText.replace(/[\n \t]+/g, " ").trim()), u.length > 0 && (u.length < 50 ? e = u : "A" === l && (e = u.slice(0, 30)));
                return e
            }, t.containerElemContent = function (t) {
                var e;
                if ("BUTTON" === t.tagName) {
                    if (t.name.length > 0) return t.name;
                    if (this.onlyContainsChildren(t, S) && null != t.textContent && (e = t.textContent.replace(/[\n \t]+/g, " ").trim(), e.length > 0 && e.length < 50)) return e
                } else if ("A" === t.tagName) {
                    if (this.hasAttr(t, "data-growing-title") && t.getAttribute("data-growing-title").length > 0) return t.getAttribute("data-growing-title");
                    if (this.hasAttr(t, "title") && t.title.length > 0) return t.getAttribute("title");
                    if (this.onlyContainsChildren(t, S) && null != t.textContent) {
                        if (e = t.textContent.replace(/[\n \t]+/g, " ").trim(), e.length > 0 && e.length < 50) return e
                    } else if (this.hasAttr(t, "href") && t.getAttribute("href").length > 0) return t.getAttribute("href")
                }
            }, t.hasAttr = function (t, e) {
                return t.hasAttribute ? t.hasAttribute(e) : !!t[e]
            }, t.isEmpty = function (t) {
                var e;
                return ! function () {
                    var n, i, r;
                    for (r = [], i = 0, n = t.length; i < n; i++) e = t[i], r.push(t.hasOwnProperty(e));
                    return r
                }()
            }, t.aElementsEqualbElements = function (t, e) {
                var n, i;
                if ("object" != typeof t || "object" != typeof e) return !1;
                if (t === e) return !0;
                if (null == t || null == e) return !1;
                for (n in t)
                    if (T.call(t, n)) {
                        if (i = t[n], !e.hasOwnProperty(n)) return !1;
                        if (i !== e[n]) return !1
                    }
                for (n in e)
                    if (T.call(e, n) && (i = e[n], !t.hasOwnProperty(n))) return !1;
                return !0
            }, t.offsetTop = function (t) {
                var e;
                if (t.getBoundingClientRect) return e = t.getBoundingClientRect().top + document.documentElement.scrollTop;
                if (!t.offsetParent) return 0;
                for (e = t.offsetTop; t = t.offsetParent;) e += t.offsetTop;
                return e
            }, t.title = function () {
                var t, e;
                return e = document.title, e.indexOf("|") !== -1 ? (t = e.split("|"), t.length >= 3 ? this.trim(t[0] + "|" + t[1]) : t[0].trim()) : e.indexOf("-") !== -1 ? this.trim(e.split("-")[0]) : e
            }, t.indexOf = function (t, e) {
                var n, i, r;
                if (null != Array.prototype.indexOf) return t.indexOf(e);
                for (i = t.length, n = -1; ++n < i;)
                    if (r = t[n], r === e) return n;
                return -1
            }, t.host = function () {
                var t, e, n, i, r, a;
                if (null != window.rules) {
                    for (a = window.location.toString(), n = 0, t = rules.length; n < t; n++) r = rules[n], i = r.split(","), a = a.replace(new RegExp(i[0]), i[1]);
                    return e = g.exec(a), e[10]
                }
                return window.location.host
            }, t.path = function () {
                var t, e, n, i, r, a, s, o;
                if (null != window.rules) {
                    for (o = window.location.toString(), r = 0, e = rules.length; r < e; r++) s = rules[r], a = s.split(","), o = o.replace(new RegExp(a[0]), a[1]);
                    n = g.exec(o), i = n[13], null == i && (i = "")
                } else i = window.location.pathname;
                return i = this.normalizePath(i), window.vds.hashtag ? (t = window.location.hash, i += t.indexOf("?") !== -1 ? t.split("?")[0] : t) : i
            }, t.query = function () {
                var t, e, n, i, r, a, s;
                if (null != window.rules) {
                    for (s = window.location.toString(), n = 0, t = rules.length; n < t; n++) a = rules[n], r = a.split(","), s = s.replace(new RegExp(r[0]), r[1]);
                    e = g.exec(s), i = e[16], null == i && (i = "")
                } else i = window.location.search;
                return this.normalizeQuery(i)
            }, t.normalizePath = function (t) {
                var e, n;
                return e = this.trim(t), n = e.length, n > 1 && "/" === e[n - 1] ? e.slice(0, n - 1) : e
            }, t.normalizeQuery = function (t) {
                var e;
                return e = t.length, e > 1 && "?" === t[0] ? t.slice(1) : window.vds.hashtag && window.location.hash.indexOf("?") !== -1 ? window.location.hash.split("?")[1] : t
            }, t.css = function (t, e, n) {
                var i, r, a, s, o, h, l, u;
                if (t) {
                    if (arguments.length < 3) {
                        if (i = getComputedStyle(t, ""), "string" == typeof e) return t.style[v(e)] || i.getPropertyValue(e);
                        if (C(e)) {
                            for (h = {}, l = 0, s = e.length; l < s; l++) o = e[l], h[o] = t.style[v(o)] || i.getPropertyValue(o);
                            return h
                        }
                    }
                    if (r = "", "string" == typeof e) return n || 0 === n ? t.style[w(e)] = _(e, n) : t.style[w(e)] = "";
                    u = [];
                    for (a in e) e[a] || 0 === e[a] ? u.push(t.style[w(a)] = _(a, e[a])) : u.push(t.style[w(a)] = "");
                    return u
                }
            }, t.hasBackgroundImage = function (e) {
                var n;
                return n = t.css(e, "background-image"), n && n.length > 0 && "none" !== n
            }, t.cursorOffset = function (t) {
                var e, n;
                return n = {
                    top: 0,
                    left: 0
                }, t.pageX ? (n.left = t.pageX, n.top = t.pageY) : t.clientX && (e = document.documentElement || document.body, n.left = t.clientX + e.scrollLeft, n.top = t.clientY + e.scrollTop), n
            }, t.merge = function (t, e) {
                var n, i;
                i = {};
                for (n in t) i[n] = t[n];
                for (n in e) i[n] = e[n];
                for (n in i) "function" == typeof i[n] && delete i[n];
                return i
            }, t.trim = function (t) {
                return t.replace(/^\s+/, "").replace(/\s+$/, "")
            }, t.levelDomain = function (t) {
                var e;
                return e = t.split("."), 2 === e.length ? "." + e.join(".") : e.length >= 3 && "com" === e[e.length - 2] ? "." + e.slice(-3).join(".") : "." + e.slice(-2).join(".")
            }, t.isValidGrTrackingTag = function (t) {
                return "password" !== t.type && t.hasAttribute("growing-track")
            }, t.isClickableInputTag = function (t) {
                return "INPUT" === t.tagName && ["button", "submit"].indexOf(t.type) !== -1
            }, t.uniq = function (t) {
                var e, n, i, r;
                if (r = [], t instanceof Array == !1 || t.length <= 1) return t;
                for (i = 0, n = t.length; i < n; i++) e = t[i], r.indexOf(e) === -1 && r.push(e);
                return r
            }, t.clone = function (t) {
                var e, n, i;
                if (null == t || "object" != typeof t) return t;
                if (t instanceof Date) return new Date(t.getTime());
                if (t instanceof RegExp) return e = "", null != t.global && (e += "g"), null != t.ignoreCase && (e += "i"), null != t.multiline && (e += "m"), null != t.sticky && (e += "y"), new RegExp(t.source, e);
                i = {};
                for (n in t) i[n] = t[n];
                return i
            }, t.skeletonXpath = function (t) {
                var e, n;
                return e = t.split("/").splice(1), n = e.filter(function (t) {
                    return function (t) {
                        return !t.match(/^[a-zA-Z]+((\.|\#)?\*)?$/)
                    }
                }(this)), 0 === n.length
            }, t.filterSkeletonXpath = function (e) {
                var n;
                return n = e.split(","), n.filter(function (e) {
                    return function (e) {
                        return !t.skeletonXpath(e)
                    }
                }(this)).join(",")
            }, t.getPercent = function (t) {
                var e;
                return e = Math.round(1e4 * t) / 100, 0 === e && (e = "< 0.01"), e
            }, t.filterProtocol = function (t) {
                var e;
                return t ? (e = t.indexOf("://"), e === -1 ? t : t.substring(e + 3)) : ""
            }, t
        }(), r = function () {
            function t(t, e, n) {
                this._similarity = k(this._similarity, this), this.getClosedList = k(this.getClosedList, this), this.positive = t, this.negativeList = e, this.cands = n
            }
            return t.prototype.getClosedList = function () {
                var t, e, n, i, r, a, s, o, h, l;
                for (h = [], s = this.cands, a = 0, e = s.length; a < e; a++) {
                    for (t = s[a], r = 0, o = this.negativeList, l = 0, n = o.length; l < n; l++) i = o[l], r = Math.max(0, this._similarity(i, t));
                    this._similarity(this.positive, t) > r && h.push(t)
                }
                return h
            }, t.prototype._similarity = function (t, e) {
                var n, i, r, a, s;
                if (t = t.split("/"), e = e.split("/"), t.length !== e.length) return 0;
                for (s = 0, r = a = 0, n = t.length; a < n; r = ++a) i = t[r], s += this._lcs(i, e[r]);
                return s
            }, t.prototype._lcs = function (t, e) {
                var n, i, r, a, s, o, h, l, u, c, d, p, f, g, m;
                for (t = t.split(""), e = e.split(""), p = [t, e].sort(function (t, n) {
                    return t.length <= e.length
                }), f = p[0], s = p[1], n = [], a = 0; a <= f.length;) n[a] = 0, a++;
                for (i = d = 0, o = s.length; d < o; i = ++d)
                    for (l = s[i], c = 0, r = m = 0, h = f.length; m < h; r = ++m) u = f[r], g = n[r + 1], n[r + 1] = Math.max(n[r + 1], n[r]), l === u && (n[r + 1] = Math.max(n[r + 1], c + 1)), c = g;
                return n[n.length - 1]
            }, t
        }(), u = function () {
            function t(t) {
                this.onCorrectXpath = k(this.onCorrectXpath, this), this.send = k(this.send, this), this.valid = k(this.valid, this), this.context = t
            }
            return t.prototype.valid = function () {
                var t, e;
                return t = this.context.circleEvent.event, t.patternMatched && !(null != (e = t.currentTargetObject) ? e.id : void 0)
            }, t.prototype.send = function (t) {
                return this.context.chartContainer.renderLoading(), this.context.circleSend({
                    attrs: t.attrs,
                    tna: "messageCenter",
                    sna: window.grSource.name,
                    sid: window.grSource.id
                }, "get-matched-patterns")
            }, t.prototype.onCorrectXpath = function (t) {
                var e, n, i, a, s, o, h, l, u, c, d, p, f, g, v, y, w, x, C;
                if (this.context.circleEvent) {
                    if (a = this.context.circleEvent.event, p = [], u = [], i = [], n = [], "chng" === a.actions[0])
                        for (f = 0, o = t.length; f < o; f++) c = t[f], g = c.match(/\s->\s\((0,0)\)/), c = c.replace(/\s->\s\(\d,\d\)/, ""), g ? (e = a.attrs.xpath.split("/"), d = c.split("/"), d[d.length - 1] = e[e.length - 1], p.push(d.join("/"))) : c.split("/")[c.length - 1] === a.attrs.xpath.split("/")[c.length - 1] ? u.push(c) : n.push(c);
                    else
                        for (w = 0, h = t.length; w < h; w++) c = t[w], g = c.match(/\s->\s\((0,0|1,0|0,1)\)/), c = c.replace(/\s->\s\(\d,\d\)/, ""), g ? p.push(c) : n.push(c);
                    for (x = 0, l = n.length; x < l; x++) c = n[x], y = m.getAccurateElementsByXpath(c, a.attrs.xpath, a.attrs.content, a.attrs.domIndex, a.attrs.href), y.length > 0 ? null != a.attrs && c !== a.attrs.xpath ? u.push(c) : p.push(c) : i.push(c);
                    return C = new r(p[0], u, i), v = C.getClosedList().concat(p), a = this.context.circleEvent.event, this.context.circleEvent.updateFilter("xpath", v.join(","), !0), (null != a ? a.id : void 0) || (s = this.context.filterContainer.initFilterByTab(a, this.context.filterContainer.calcTab(a)), this.context.circleEvent.update("filter", s)), this.context.circleEvent.update("correctXpath", v.join(",")), this.context.show(this.context.circleEvent.event, this.context.circleEvent.event.currentTargetObject.target, !1)
                }
            }, t
        }(), e = function () {
            function t() {
                this.cleanAll = k(this.cleanAll, this), this.clickClass = "growing-circle-clicked", this.hoverClass = "growing-circle-hovered", this.similarClass = "growing-circle-similar", this.hiddenClass = "growing-circle-hidden", this.taggedClass = "growing-circle-tagged", this.taggedSimilarClass = "growing-circle-tagged-similar", this.coverClass = "growing-circle-cover", this.taggedCoverClass = "growing-circle-tagged-cover", this.hoverCoverClass = "growing-circle-hover-cover", this.simpleMode = !0, MQ.subscribe("CircleStyle::click-style-clean-all", this.cleanAll)
            }
            return t.prototype.toggleMode = function () {
                return this.simpleMode = !this.simpleMode
            }, t.prototype.circleable = function (t) {
                var e, n, i, r, a;
                return !t.hasAttribute("data-growing-ingore") && (("INPUT" !== t.tagName || "password" !== t.type) && (!(m.hasClass(t, this.taggedCoverClass) || m.hasClass(t, this.hoverCoverClass) || m.hasClass(t, this.coverClass)) && (!this.simpleMode || (["SELECT", "A", "BUTTON", "INPUT", "IMG"].indexOf(t.tagName) !== -1 || (m.isLeaf(t) && (null != (n = t.innerText) ? n.trim().length : void 0) < 50 ? !!t.hasAttribute("data-growing-circle") || !(0 === (null != (i = t.innerText) ? i.trim().length : void 0) && (a = m.width(t), e = m.height(t), a > .5 * window.innerWidth && e >= .5 * window.innerHeight)) : !(!m.isParentOfLeaf(t) || !((null != (r = t.innerText) ? r.trim().length : void 0) > 0 || t.hasAttributes())) || (!(!m.hasBackgroundImage(t) || !m.lessThanSomeLevelDepth(t, 4)) || !!t.hasAttribute("data-growing-container")))))))
            }, t.prototype.getClick = function () {
                return document.getElementsByClassName(this.clickClass)[0]
            }, t.prototype.isCover = function (t) {
                return m.hasClass(t, this.coverClass)
            }, t.prototype.isClick = function (t) {
                return m.hasClass(t, this.clickClass)
            }, t.prototype.findTarget = function (t) {
                return m.hasClass(t, this.hoverCoverClass) || m.hasClass(t, this.coverClass) ? t = t.parentNode.querySelector("*[data-target=" + t.getAttribute("data-orig") + "]") : m.hasClass(t, this.taggedCoverClass) ? t = t.parentNode.querySelector("*[data-tagged-target=" + t.getAttribute("data-orig") + "]") : t
            }, t.prototype.clickOn = function (t) {
                var e;
                return t = this.findTarget(t), !!this.circleable(t) && (e = randomString(8), this.appendCover(t, this.coverClass, "click-" + e), m.addClass(t, this.clickClass), t.setAttribute("data-target", "click-" + e), t)
            }, t.prototype.clickOff = function (t) {
                var e, n, i, r, a;
                for (m.removeClass(t, this.clickClass), r = document.querySelectorAll("." + this.coverClass), a = [], i = 0, n = r.length; i < n; i++) e = r[i], a.push(e.remove());
                return a
            }, t.prototype.isHover = function (t) {
                return m.hasClass(t, this.hoverClass)
            }, t.prototype.hoverIn = function (t) {
                if (!m.hasClass(t, this.hoverCoverClass)) return this.circleable(t) ? m.addClass(t, this.hoverClass) : void 0
            }, t.prototype.hoverOut = function (t) {
                return m.removeClass(t, this.hoverClass)
            }, t.prototype.isSimilar = function (t) {
                return m.hasClass(t, this.similarClass)
            }, t.prototype.similarOn = function (t) {
                return m.addClass(t, this.similarClass)
            }, t.prototype.similarOff = function (t) {
                return m.removeClass(t, this.similarClass)
            }, t.prototype.hide = function (t) {
                return m.addClass(t, this.hiddenClass)
            }, t.prototype.show = function (t) {
                return m.removeClass(t, this.hiddenClass)
            }, t.prototype.isTagged = function (t) {
                return m.hasClass(t, this.taggedClass)
            }, t.prototype.taggedOn = function (t, e) {
                var n;
                return m.hasClass(t, this.taggedClass) ? !m.hasAttr(t, "data-tag-id") && e ? t.setAttribute("data-tag-id", e.id) : void 0 : (this.taggedSimilarOff(t), n = randomString(8), this.appendCover(t, this.taggedCoverClass, "tagged-" + n), m.addClass(t, this.taggedClass), e && t.setAttribute("data-tag-id", e.id), t.setAttribute("data-tagged-target", "tagged-" + n))
            }, t.prototype.taggedOff = function (t) {
                return m.removeClass(t, this.taggedClass)
            }, t.prototype.taggedSimilarOn = function (t) {
                if (!m.hasClass(t, this.taggedClass)) return m.addClass(t, this.taggedSimilarClass)
            }, t.prototype.taggedSimilarOff = function (t) {
                return m.removeClass(t, this.taggedSimilarClass)
            }, t.prototype.cleanClick = function () {
                var t, e, n, i, r;
                for (i = document.querySelectorAll("." + this.clickClass), r = [], n = 0, e = i.length; n < e; n++) t = i[n], r.push(this.clickOff(t));
                return r
            }, t.prototype.cleanHover = function () {
                var t, e, n, i, r;
                for (i = document.querySelectorAll("." + this.hoverClass), r = [], n = 0, e = i.length; n < e; n++) t = i[n], r.push(this.hoverOut(t));
                return r
            }, t.prototype.cleanSimilar = function () {
                var t, e, n, i, r;
                for (i = document.querySelectorAll("." + this.similarClass), r = [], n = 0, e = i.length; n < e; n++) t = i[n], r.push(this.similarOff(t));
                return r
            }, t.prototype.cleanHidden = function () {
                var t, e, n, i, r;
                for (i = document.querySelectorAll("." + this.hiddenClass), r = [], n = 0, e = i.length; n < e; n++) t = i[n], r.push(this.show(t));
                return r
            }, t.prototype.cleanTagged = function () {
                var t, e, n, i, r, a, s, o, h;
                for (a = document.querySelectorAll("." + this.taggedClass), r = 0, n = a.length; r < n; r++) e = a[r], this.taggedOff(e);
                for (s = document.querySelectorAll("." + this.taggedCoverClass), o = [], h = 0, i = s.length; h < i; h++) t = s[h], o.push(t.remove());
                return o
            }, t.prototype.cleanTaggedSimilar = function () {
                var t, e, n, i, r;
                for (i = document.querySelectorAll("." + this.taggedSimilarClass), r = [], n = 0, e = i.length; n < e; n++) t = i[n], r.push(this.taggedSimilarOff(t));
                return r
            }, t.prototype.cleanAll = function () {
                return this.cleanClick(), this.cleanHover(), this.cleanSimilar(), this.cleanHidden()
            }, t.prototype.appendCover = function (t, e, n) {
                var i, r, a;
                if ((r = t.parentNode) && (a = m.position(t))) {
                    for (i = document.createElement("div"), i.className = e, i.style.width = m.width(t) + "px", i.style.height = m.height(t) + "px", i.style.left = a.left + "px", i.style.top = a.top + "px", i.style.position = "absolute", n && i.setAttribute("data-orig", n); null != r && ["TABLE", "TR", "TD", "TH"].indexOf(r.tagName) !== -1;) r = r.parentNode;
                    if (r) return r.appendChild(i)
                }
            }, t
        }(), s = function () {
            function t() {}
            return t.parseDoms = function (e, n) {
                var i;
                return i = [], e.map(function (e) {
                    var r, a, s, o, h;
                    if (!(e.items.length < 1)) {
                        for (a = t._findByXpath(e), h = [], o = 0, s = a.length; o < s; o++) r = a[o], r && (r.h || r.v) && n.circleable(r.node) && r.node.offsetParent ? h.push(i.push(r)) : h.push(void 0);
                        return h
                    }
                }), t.sort(i)
            }, t._findByXpath = function (e) {
                var n, i, r, a;
                switch (e.items.length) {
                case 0:
                    return [];
                case 1:
                    return t.formatDoms(m.getElementsBySkeletonXpath(e.x, e.v, e.items[0].idx, e.h, !0), e, e.items[0]);
                default:
                    return i = m.getElementsBySkeletonXpath(e.x, e.v, null, e.h, !0), 0 === i.length ? [] : i.length > 1 ? (r = t.domsGroupByIndex(i), n = [], e.items.map(function (i) {
                        return n = n.concat(t.formatDoms(r[i.idx], e, i))
                    }), n) : (a = t.polymerData(e), t.formatDoms(m.getElementsBySkeletonXpath(a.x, a.v, null, a.h, !0), a, a.item))
                }
            }, t.sort = function (t) {
                var e;
                return e = t.sort(function (t, e) {
                    return e.cnt - t.cnt
                }), e || []
            }, t.clean = function (t) {
                var e;
                return e = t.map(function (t) {
                    return m.clone(t)
                }), 0 !== e.length && e ? e.map(function (t) {
                    return delete t.node, t
                }) : []
            }, t.domsGroupByIndex = function (t) {
                var e;
                return e = [], t.map(function (t) {
                    return e[m.index(t)] ? e[m.index(t)].push(t) : e[m.index(t)] = [t]
                }), e
            }, t.polymerData = function (t) {
                var e, n;
                return n = 0, e = 0, t.items.map(function (t) {
                    return n += t.percent, e += t.cnt
                }), {
                    v: t.v,
                    h: t.h,
                    x: t.x,
                    item: {
                        idx: 0,
                        cnt: e,
                        percent: n
                    }
                }
            }, t.formatDoms = function (e, n, i) {
                return e ? e.map(function (e) {
                    return t.dom(e, n, i)
                }) : []
            }, t.dom = function (t, e, n) {
                return {
                    node: t,
                    v: e.v,
                    h: e.h,
                    x: e.x,
                    cnt: n.cnt,
                    idx: n.idx,
                    percent: n.percent
                }
            }, t.highlight = function (t, e, n) {
                var i, r, a, s;
                if (t.node) return s = Math.floor(window.innerHeight / 2 - t.node.getBoundingClientRect().top), e.cleanHover(), e.hoverIn(t.node), i = t.node.getBoundingClientRect(), r = i.left + i.width / 2, a = i.top + i.height / 2, 0 === s ? n(r, a) : (window.scrollBy(0, -s), n(r, a))
            }, t
        }(), a = function () {
            function t(t) {
                this.closeMessage = k(this.closeMessage, this), this.showMessage = k(this.showMessage, this), this.turnOff = k(this.turnOff, this), this.repaint = k(this.repaint, this), this.onClick = k(this.onClick, this), this.onMouseout = k(this.onMouseout, this), this.onMousemove = k(this.onMousemove, this), this.updateTooltips = k(this.updateTooltips, this), this.turnOn = k(this.turnOn, this), this.rankElemClick = k(this.rankElemClick, this), this.bindEvents = k(this.bindEvents, this), this.createMessageChannel = k(this.createMessageChannel, this), this.createCanvas = k(this.createCanvas, this), this.createTips = k(this.createTips, this), this.initialElements = k(this.initialElements, this), this.context = t, this.circleStyle = new e, this.data = this.timer = null, this.hasBindEvent = !1, this.initialElements()
            }
            return t.prototype.initialElements = function () {
                return this.heatMapWrapper = document.querySelector("body"), this.createCanvas(), this.createTips(), this.createMessageChannel()
            }, t.prototype.createTips = function () {
                return this.tooltips = document.createElement("div"), this.tooltips.classList.add("heatmap-tooltips"), document.body.appendChild(this.tooltips)
            }, t.prototype.createCanvas = function () {
                return document.querySelector(".heatmap-canvas") && document.querySelector(".heatmap-canvas").remove(), this.heatmapCanvas = document.createElement("canvas"), this.heatmapCanvas.className = "heatmap-canvas", this.heatMapWrapper.appendChild(this.heatmapCanvas), this._heatmapInstance = new h({
                    container: this.heatMapWrapper,
                    canvas: this.heatmapCanvas
                })
            }, t.prototype.createMessageChannel = function () {
                return this.messageContainer = document.createElement("div"), this.messageContainer.classList.add("heatmap-message"), document.body.appendChild(this.messageContainer)
            }, t.prototype.bindEvents = function () {
                return m.bindOn(this.heatmapCanvas, "mousemove", this.onMousemove), m.bindOn(this.heatmapCanvas, "mouseout", this.onMouseout),
                    m.bindOn(this.heatmapCanvas, "click", this.onClick), m.bindOn(window, "mousewheel", this.repaint), m.bindOn(window, "scroll", this.repaint), m.bindOn(window, "resize", this.repaint), this.hasBindEvent = !0
            }, t.prototype.rankElemClick = function (t) {
                var e, n;
                if (n = this.dashbordDoms.filter(function (e) {
                    return function (e) {
                        return e.x === t.x && e.h === t.h && e.v === t.v && e.percent === t.percent && e.indx === t.indx && e.cnt === t.cnt
                    }
                }(this)), n[0]) return e = m.clone(n[0]), e.percent = m.getPercent(e.percent), s.highlight(e, this.circleStyle, function (t) {
                    return function (n, i) {
                        return t.tooltips.style.display = "block", t.updateTooltips(n, i, e)
                    }
                }(this))
            }, t.prototype.turnOn = function (t) {
                var e, n, i;
                return this.hasBindEvent || this.bindEvents(), i = [], this.data = t, n = 1, this.dashbordDoms = [], e = s.parseDoms(t.data, this.circleStyle), e.map(function (t) {
                    return function (e) {
                        var r, a, s, o;
                        if (e.cnt > n && (n = e.cnt), t.dashbordDoms.length < 16 && t.dashbordDoms.push(e), a = e.node.getBoundingClientRect(), a.width * a.height !== 0 && window.innerHeight > (s = a.top) && s > -(a.height / 2) && window.innerWidth > (o = a.left) && o > -(a.width / 2)) return r = {
                            x: t.point(a.left + a.width / 2),
                            y: t.point(a.top + a.height / 2),
                            value: {
                                number: e.cnt,
                                node: e.node,
                                pos: a,
                                content: e.v,
                                index: e.idx,
                                href: e.h,
                                xpath: e.x,
                                percent: m.getPercent(e.percent)
                            }
                        }, i.push(r)
                    }
                }(this)), this.context.circleSend({
                    sid: window.grSource.id,
                    sna: window.grSource.name,
                    tna: "messageCenter"
                }, "drawn-heatmap"), this.context.circleSend({
                    nodes: s.clean(this.dashbordDoms),
                    sid: window.grSource.id,
                    sna: window.grSource.name,
                    tna: "messageCenter"
                }, "preview-dashboard-data"), 0 === i.length ? this.showMessage("msg", "该可视区域暂无点击数据") : (document.querySelector(".heatmap-canvas") || (this.initialElements(), this.bindEvents()), this._heatmapInstance.setData({
                    max: n,
                    data: i
                }), this.closeMessage(), this.heatmapCanvas.style.display = "block")
            }, t.prototype.updateTooltips = function (t, e, n) {
                var i, r, a;
                return i = "", window.heatmap_debug_mode && (i = "点击量：" + n.number), i = "点击率(点击数/PV)：" + n.percent + "%<br>" + i, n.index && window.heatmap_debug_mode && (i = "Index：" + n.index + "<br>" + i), n.content && window.heatmap_debug_mode && (i = "内容：" + (n.content || n.href || n.h || n.v) + "<br>" + i), n.href && window.heatmap_debug_mode && (i = "链接：" + n.href + "<br>" + i), n.xpath && window.heatmap_debug_mode && (i = "xpath：" + n.xpath + "<br>" + i), this.tooltips.innerHTML = i, r = this.tooltips.getBoundingClientRect(), t + r.width > window.innerWidth && (t = t - r.width - 15), e + r.height > window.innerHeight && (e = e - r.height - 15), a = "translate(" + (t + 15) + "px, " + (e + 15) + "px)", this.tooltips.style.webkitTransform = a
            }, t.prototype.onMousemove = function (t) {
                var e;
                return e = this._heatmapInstance.getValueAt({
                    x: this.point(t.offsetX),
                    y: this.point(t.offsetY)
                }), e && e.node ? (m.hasClass(document.body, "vds-entrytext") && (this.circleStyle.cleanHover(), e.node.dispatchEvent(this.fakeEvent("mouseover"))), this.tooltips.style.display = "block", this.updateTooltips(t.clientX, t.clientY, e)) : (this.circleStyle.cleanHover(), this.tooltips.style.display = "none")
            }, t.prototype.point = function (t) {
                return 10 * Math.round(t / 10)
            }, t.prototype.onMouseout = function () {
                return this.tooltips.style.display = "none", this.circleStyle.cleanHover()
            }, t.prototype.onClick = function (t) {
                var e;
                if (e = this._heatmapInstance.getValueAt({
                    x: this.point(t.offsetX),
                    y: this.point(t.offsetY)
                }), e && e.node && m.hasClass(document.body, "vds-entrytext")) return e.node.dispatchEvent(this.fakeEvent("click"))
            }, t.prototype.fakeEvent = function (t) {
                return new MouseEvent(t, {
                    view: window,
                    bubbles: !0,
                    cancelable: !0,
                    detail: 1
                })
            }, t.prototype.repaint = function (t) {
                if (0 !== t.wheelDelta) return this.timer ? clearTimeout(this.timer) : (this.showMessage("loading", null, t.shiftKey), this.tooltips.style.display = "none", this.circleStyle.cleanHover()), this.timer = setTimeout(function (t) {
                    return function () {
                        return t.turnOn(t.data), t.timer = null
                    }
                }(this), 1e3)
            }, t.prototype.turnOff = function () {
                if (m.unbind(this.heatmapCanvas, "mousemove", this.onMousemove), m.unbind(this.heatmapCanvas, "mouseout", this.onMouseout), m.unbind(this.heatmapCanvas, "click", this.onClick), m.unbind(window, "mousewheel", this.repaint), m.unbind(window, "scroll", this.repaint), m.unbind(window, "resize", this.repaint), this.hasBindEvent = !1, this.circleStyle.cleanHover(), this.timer && clearTimeout(this.timer), this.tooltips && (this.tooltips.style.display = "none"), this.heatmapCanvas && (this.heatmapCanvas.style.display = "none"), this.messageContainer) return this.messageContainer.style.display = "none"
            }, t.prototype.showMessage = function (t, e, n) {
                switch (null == n && (n = !1), this.messageContainer.innerHTML = "", this.messageContainer.style.display = "block", this.heatmapCanvas.style.display = "none", this._heatmapInstance._renderer._clear(), t) {
                case "loading":
                    return m.removeClass(this.messageContainer, "msg"), m.removeClass(this.messageContainer, "heatmap-message-loading-with-shift-" + !n), m.addClass(this.messageContainer, "heatmap-message-loading-with-shift-" + n);
                default:
                    return this.messageContainer.setAttribute("data-msg", e), m.removeClass(this.messageContainer, "heatmap-message-loading-with-shift-" + n), m.removeClass(this.messageContainer, "heatmap-message-loading-with-shift-" + !n), m.addClass(this.messageContainer, "msg")
                }
            }, t.prototype.closeMessage = function () {
                return this.messageContainer.style.display = "none"
            }, t
        }(), t = function () {
            function t(t) {
                var n, i, r, a;
                this.config = t, i = t.container, a = this.shadowCanvas = document.createElement("canvas"), n = this.canvas = t.canvas, r = this._renderBoundaries = [1e4, 1e4, 0, 0], this._setCanvasBoundary(), this.shadowCtx = a.getContext("2d"), this.ctx = n.getContext("2d"), this._palette = e(t), this._templates = {}, this._setStyles(t)
            }
            var e, n, i;
            return t.prototype._setCanvasBoundary = function () {
                return this._width = this.canvas.width = this.shadowCanvas.width = this.config.width || window.innerWidth, this._height = this.canvas.height = this.shadowCanvas.height = this.config.height || window.innerHeight
            }, t.prototype.renderPartial = function (t) {
                if (t.data.length > 0) return this._drawAlpha(t), this._colorize()
            }, t.prototype.renderAll = function (t) {
                if (this._clear(), this._setCanvasBoundary(), t.data.length > 0) return this._drawAlpha(i(t)), this._colorize()
            }, t.prototype._updateGradient = function (t) {
                return this._palette = e(t)
            }, t.prototype.updateConfig = function (t) {
                return t.gradient && this._updateGradient(t), this._setStyles(t)
            }, t.prototype.setDimensions = function (t, e) {
                return this._width = t, this._height = e, this.canvas.width = this.shadowCanvas.width = t, this.canvas.height = this.shadowCanvas.height = e
            }, t.prototype._clear = function () {
                return this.shadowCtx.clearRect(0, 0, this._width, this._height), this.ctx.clearRect(0, 0, this._width, this._height)
            }, t.prototype._setStyles = function (t) {
                return this._blur = 0 === t.blur ? 0 : t.blur || t.defaultBlur, t.backgroundColor && (this.canvas.style.backgroundColor = t.backgroundColor), this._width = this.canvas.width = this.shadowCanvas.width = t.width || this._width, this._height = this.canvas.height = this.shadowCanvas.height = t.height || this._height, this._opacity = 255 * (t.opacity || 0), this._maxOpacity = 255 * (t.maxOpacity || t.defaultMaxOpacity), this._minOpacity = 255 * (t.minOpacity || t.defaultMinOpacity), this._useGradientOpacity = !!t.useGradientOpacity
            }, t.prototype._drawAlpha = function (t) {
                var e, i, r, a, s, o, h, l, u, c, d, p;
                for (a = this._min = t.min, r = this._max = t.max, t = t.data || [], i = t.length, e = 1 - this._blur, l = []; i--;) s = t[i], p = Math.min(s.value.number, r), o = s.value.pos.left, h = s.value.pos.top, u = this.shadowCtx, d = n(s, e), c = (s.value.number - a) / (r - a), c = c < .01 ? .01 : c, u.globalAlpha = .4 + .6 * c, u.drawImage(d, o, h), o < this._renderBoundaries[0] && (this._renderBoundaries[0] = o), h < this._renderBoundaries[1] && (this._renderBoundaries[1] = h), o + s.value.pos.width > this._renderBoundaries[2] && (this._renderBoundaries[2] = o + s.value.pos.width), h + s.value.pos.height > this._renderBoundaries[3] ? l.push(this._renderBoundaries[3] = h + s.value.pos.height) : l.push(void 0);
                return l
            }, t.prototype._colorize = function () {
                var t, e, n, i, r, a, s, o, h, l, u, c, d, p, f, g, m, v;
                for (m = this._renderBoundaries[0], v = this._renderBoundaries[1], g = this._renderBoundaries[2] - m, n = this._renderBoundaries[3] - v, l = this._width, o = this._height, d = this._opacity, h = this._maxOpacity, u = this._minOpacity, f = this._useGradientOpacity, m < 0 && (m = 0), v < 0 && (v = 0), m + g > l && (g = l - m), v + n > o && (n = o - v), r = this.shadowCtx.getImageData(m, v, g, n), a = r.data, s = a.length, p = this._palette, i = 3; i < s;) t = a[i], c = 4 * t, c ? (e = void 0, e = d > 0 ? d : t < h ? t < u ? u : t : h, a[i - 3] = p[c], a[i - 2] = p[c + 1], a[i - 1] = p[c + 2], a[i] = f ? p[c + 3] : e, i += 4) : i += 4;
                return r.data = a, this.ctx.putImageData(r, m, v), this._renderBoundaries = [1e3, 1e3, 0, 0]
            }, t.prototype.getValueAt = function (t) {
                var e, n, i, r, a;
                return n = this.shadowCtx.getImageData(t.x, t.y, 1, 1), e = n.data[3], i = this._max, r = this._min, a = Math.abs(i - r) * e / 255 >> 0
            }, t.prototype.getDataURL = function () {
                return this.canvas.toDataURL()
            }, e = function (t) {
                var e, n, i, r, a;
                n = t.gradient || t.defaultGradient, r = document.createElement("canvas"), a = r.getContext("2d"), r.width = 256, r.height = 1, e = a.createLinearGradient(0, 0, 256, 1);
                for (i in n) e.addColorStop(i, n[i]);
                return a.fillStyle = e, a.fillRect(0, 0, 256, 1), a.getImageData(0, 0, 256, 1).data
            }, n = function (t, e) {
                var n, i, r, a, s, o, h, l, u, c;
                return h = document.createElement("canvas"), l = h.getContext("2d"), h.width = t.value.pos.width, h.height = t.value.pos.height, u = n = t.value.pos.width / 2, c = i = t.value.pos.height / 2, a = n > i ? n : i, s = n / a, o = i / a, l.scale(s, o), r = l.createRadialGradient(u / s, c / o, a * e, u / s, c / o, a), r.addColorStop(0, "rgba(0,0,0,1)"), r.addColorStop(1, "rgba(0,0,0,0)"), l.fillStyle = r, l.fillRect(0, 0, t.value.pos.width / s, t.value.pos.height / o), h
            }, i = function (t) {
                var e, n, i, r, a, s, o, h, l, u;
                for (i = [], n = t.min, e = t.max, t = t.data, s = Object.keys(t), o = s.length; o--;)
                    for (a = s[o], l = Object.keys(t[a]), u = l.length; u--;) h = l[u], r = t[a][h], i.push({
                        x: a,
                        y: h,
                        value: r
                    });
                return {
                    min: n,
                    max: e,
                    data: i
                }
            }, t
        }(), o = {
            defaultRenderer: "canvas2d",
            defaultGradient: {.25: "rgb(0,0,255)", .55: "rgb(0,255,0)", .85: "yellow", 1: "rgb(255,0,0)"
            },
            defaultMaxOpacity: 1,
            defaultMinOpacity: 0,
            defaultBlur: .85,
            defaultXField: "x",
            defaultYField: "y",
            defaultValueField: "value"
        }, h = function () {
            function e() {
                var e;
                e = this._config = m.merge(o, arguments[0] || {}), this._coordinator = new n, this._renderer = new t(e), this._store = new c(e), i(this)
            }
            var n, i;
            return e.prototype.addData = function () {
                return this._store.addData.apply(this._store, arguments), this
            }, e.prototype.removeData = function () {
                return this._store.removeData && this._store.removeData.apply(this._store, arguments), this
            }, e.prototype.setData = function () {
                return this._store.setData.apply(this._store, arguments), this
            }, e.prototype.setDataMax = function () {
                return this._store.setDataMax.apply(this._store, arguments), this
            }, e.prototype.setDataMin = function () {
                return this._store.setDataMin.apply(this._store, arguments), this
            }, e.prototype.configure = function (t) {
                return this._config = m.merge(this._config, t), this._renderer.updateConfig(this._config), this._coordinator.emit("renderall", this._store._getInternalData()), this
            }, e.prototype.repaint = function () {
                return this._coordinator.emit("renderall", this._store._getInternalData()), this
            }, e.prototype.getData = function () {
                return this._store.getData()
            }, e.prototype.getDataURL = function () {
                return this._renderer.getDataURL()
            }, e.prototype.getValueAt = function (t) {
                return this._store.getValueAt ? this._store.getValueAt(t) : this._renderer.getValueAt ? this._renderer.getValueAt(t) : null
            }, n = function () {
                function t() {
                    this.emit = k(this.emit, this), this.on = k(this.on, this), this.cStore = {}
                }
                return t.prototype.on = function (t, e, n) {
                    var i;
                    return i = this.cStore, i[t] || (i[t] = []), i[t].push(function (t) {
                        return function (t) {
                            return e.call(n, t)
                        }
                    }(this))
                }, t.prototype.emit = function (t, e) {
                    var n, i, r, a, s;
                    if (n = this.cStore, n[t]) {
                        for (a = n[t].length, r = 0, s = []; r < a;) i = n[t][r], i(e), s.push(r++);
                        return s
                    }
                }, t
            }(), i = function (t) {
                var e, n, i;
                return n = t._renderer, e = t._coordinator, i = t._store, e.on("renderpartial", n.renderPartial, n), e.on("renderall", n.renderAll, n), e.on("extremachange", function (e) {
                    return t._config.onExtremaChange && t._config.onExtremaChange({
                        min: e.min,
                        max: e.max,
                        gradient: t._config.gradient || t._config.defaultGradient
                    })
                }), i.setCoordinator(e)
            }, e
        }(), c = function () {
            function t(t) {
                this._coordinator = {}, this._data = [], this._min = 0, this._max = 1, this._xField = t.xField || t.defaultXField, this._yField = t.yField || t.defaultYField, this._valueField = t.valueField || t.defaultValueField
            }
            return t.prototype._organiseData = function (t, e) {
                var n, i, r, a;
                return r = t[this._xField], a = t[this._yField], n = this._data, i = t[this._valueField] || 1, n[r] || (n[r] = []), n[r][a] ? (i.number = i.number + n[r][a].number, n[r][a] = i) : n[r][a] = i, i.number > this._max ? (e ? this.setDataMax(n[r][a]) : this._max = i.number, !1) : {
                    x: r,
                    y: a,
                    value: i,
                    min: this._min,
                    max: this._max
                }
            }, t.prototype._unOrganizeData = function () {
                var t, e, n, i;
                e = [], t = this._data;
                for (n in t)
                    for (i in t[n]) e.push({
                        x: n,
                        y: i,
                        value: t[n][i]
                    });
                return {
                    min: this._min,
                    max: this._max,
                    data: e
                }
            }, t.prototype._onExtremaChange = function () {
                return this._coordinator.emit("extremachange", {
                    min: this._min,
                    max: this._max
                })
            }, t.prototype.addData = function () {
                var t, e, n;
                if (arguments[0].length > 0)
                    for (t = arguments[0], e = t.length; e--;) this.addData.call(this, t[e]);
                else n = this._organiseData(arguments[0], !0), n && this._coordinator.emit("renderpartial", {
                    min: this._min,
                    max: this._max,
                    data: [n]
                });
                return this
            }, t.prototype.setData = function (t) {
                var e, n, i;
                for (e = t.data, i = e.length, this._data = [], this._radi = [], n = 0; n < i;) this._organiseData(e[n], !1), n++;
                return t.max && (this._max = t.max), this._min = t.min || 0, this._onExtremaChange(), this._coordinator.emit("renderall", this._getInternalData()), this
            }, t.prototype.removeData = function () {}, t.prototype.setDataMax = function (t) {
                return this._max = t, this._onExtremaChange(), this._coordinator.emit("renderall", this._getInternalData()), this
            }, t.prototype.setDataMin = function (t) {
                return this._min = t, this._onExtremaChange(), this._coordinator.emit("renderall", this._getInternalData()), this
            }, t.prototype.setCoordinator = function (t) {
                return this._coordinator = t
            }, t.prototype._getInternalData = function () {
                return {
                    max: this._max,
                    min: this._min,
                    data: this._data,
                    radi: this._radi
                }
            }, t.prototype.getData = function () {
                return this._unOrganizeData()
            }, t.prototype.getValueAt = function (t) {
                var e, n, i, r, a, s, o, h, l, u, c, d;
                if (l = void 0, s = 50, c = t.x, d = t.y, e = this._data, e[c] && e[c][d]) return e[c][d];
                for (u = [], n = 1; n < s;) {
                    for (r = 2 * n + 1, o = c - n, h = d - n, i = 0; i < r;) {
                        for (a = 0; a < r;) 0 === i || i === r - 1 || 0 === a || a === r - 1 ? (e[o + i] && e[o + i][h + a] && u.push(e[o + i][h + a]), a++) : a++;
                        i++
                    }
                    n++
                }
                return u.length > 0 && u[0]
            }, t
        }(), d = function () {
            function t() {
                this.tags = {}, this.screenshots = {}
            }
            return t.prototype.add = function (t) {
                return this.tags[t.id] = t
            }, t.prototype.get = function (t) {
                return this.tags[t]
            }, t.prototype.getFromDom = function (t) {
                return this.get(t.getAttribute("data-tag-id")) || this.getByAttrs(t)
            }, t.prototype.pages = function () {
                var t, e;
                e = [];
                for (t in this.tags) "page" === this.tags[t].eventType && e.push(this.tags[t]);
                return e
            }, t.prototype.addScreenshot = function (t) {
                var e;
                return e = randomString(8), this.screenshots[e] = t, e
            }, t.prototype.getScreenshot = function (t) {
                return this.screenshots[t]
            }, t.prototype.getByAttrs = function (t) {
                var e, n, i, r, a, s, o, h, l;
                o = new TaggingObject, o.make(t), n = m.host(), r = m.path(), m.query().length > 0 && (a = m.query()), e = o.value, l = o.xpath, null != o.href && (i = m.normalizePath(o.href)), h = [];
                for (s in this.tags) "object" == typeof s && h.push(this.tags[s]);
                return h.filter(function (t) {
                    return t.attrs.domain === n && t.attrs.path === r && t.attrs.query === a && t.attrs.content === e && t.attrs.xpath === l && t.attrs.href === i
                })[0]
            }, t.prototype.reset = function () {
                return this.tags = {}
            }, t
        }(), f = function () {
            function t(t, e) {
                this.resetTaggedCover = k(this.resetTaggedCover, this), this.tagStore = t, this.circleStyle = e, this.toggled = !0
            }
            return t.prototype.toggle = function (t) {
                return "undefined" != typeof t ? this.toggled = t : this.toggled = !this.toggled, this.toggled ? this.on() : this.off()
            }, t.prototype.off = function () {
                return this.circleStyle.cleanTagged(), this.circleStyle.cleanTaggedSimilar(), window.removeEventListener("resize", this.resetTaggedCoverListener)
            }, t.prototype.on = function () {
                var t, e, n, i, r, a, s;
                for (r = document.querySelectorAll("[data-tag-id]"), i = 0, n = r.length; i < n; i++) t = r[i], t.removeAttribute("data-tag-id");
                a = this.tagStore.tags;
                for (e in a) s = a[e], this.tagOn(s);
                return this.resetTaggedCoverListener = window.addEventListener("resize", this.resetTaggedCover)
            }, t.prototype.tagOn = function (t) {
                if ("page" !== t.eventType) return this.circle(t)
            }, t.prototype.tagOff = function (t) {
                if ("page" !== t.eventType) return this.uncircle(t)
            }, t.prototype.circle = function (t) {
                var e, n;
                return n = m.getElementsByXpath(t.filter.xpath, t.filter.content, t.filter.index, t.filter.href), e = m.getClickElementByTag(t), n.forEach(function (t) {
                    return function (e) {
                        if ("undefined" != typeof e) return t.circleStyle.taggedSimilarOn(e)
                    }
                }(this)), e && this.circleStyle.taggedOn(e, t), n
            }, t.prototype.uncircle = function (t) {
                var e, n;
                return n = m.getElementsByXpath(t.filter.xpath, t.filter.content, t.filter.index, t.filter.href), e = m.getClickElementByTag(t), n.forEach(function (t) {
                    return function (e) {
                        if ("undefined" != typeof e) return t.circleStyle.taggedSimilarOff(e)
                    }
                }(this)), e && this.circleStyle.taggedOff(e), n
            }, t.prototype.resetTaggedCover = function () {
                var t, e, n, i, r, a, s;
                for (r = document.querySelectorAll("." + this.circleStyle.taggedCoverClass), a = [], i = 0, n = r.length; i < n; i++) t = r[i], e = t.getAttribute("data-orig"), s = t.parentNode.querySelector("*[data-tagged-target=" + e + "]"), t.remove(), a.push(this.circleStyle.appendCover(s, this.circleStyle.taggedCoverClass, e));
                return a
            }, t
        }(), l = function () {
            function t() {
                this.calcScale = k(this.calcScale, this), this.hoverMaskView = document.createElement("div"), this.hoverMaskView.id = "vds-hybrid-mask-view", this.hoverMaskView.setAttribute("data-growing-ignore", ""), this.initScale = _vds_hybrid.scale || this.calcScale(), this.tagStore = new d, this.circleStyle = new e, this.toggleEye = new f(this.tagStore, this.circleStyle), this.xhr = new XMLHttpRequest, "complete" === document.readyState || "loaded" === document.readyState ? this.initHeatmap() : m.bind(document, "DOMContentLoaded", function (t) {
                    return function () {
                        return t.initHeatmap.apply(t, arguments)
                    }
                }(this))
            }
            var n, i;
            return i = "_bounding_rect_cache_", n = "_elem_circleable_", t.prototype.initHeatmap = function () {
                return this.heatmap = new a({
                    circleSend: function () {}
                }), delete this.heatmap.onClick
            }, t.prototype.calcScale = function () {
                var t, e, n, i, r, a, s;
                if (this.initScale = 1, n = document.querySelector('meta[name="viewport"]'), n && n.content)
                    for (r = n.content.split(","), a = 0, e = r.length; a < e; a++)
                        if (i = r[a], t = i.split("="), 2 === t.length && "initial-scale" === t[0].trim() && (s = parseFloat(t[1]), NaN !== s)) {
                            this.initScale = s;
                            break
                        }
                return window._vds_bridge && (this.initScale = window.screen.width * window.devicePixelRatio / window.innerWidth), this.initScale
            }, t.prototype.gtaHost = function () {
                return null != this.getNativeInfo().gtaHost ? this.getNativeInfo().gtaHost : "https://gta.growingio.com"
            }, t.prototype.getNativeInfo = function () {
                return null != this.nativeInfo ? this.nativeInfo : window._vds_hybrid_native_info
            }, t.prototype.onCorrectXPath = function (t) {
                var e, n, i, a, s, o, h, l, u, c, d, p, f;
                for (h = [], s = [], n = [], e = [], d = 0, i = t.length; d < i; d++) o = t[d], l = o.match(/\s->\s\((0,0|1,0|0,1)\)/), o = o.replace(/\s->\s\(\d,\d\)/, ""), l ? h.push(o) : e.push(o);
                for (p = 0, a = e.length; p < a; p++) o = e[p], c = m.getAccurateElementsByXpath(o, this.patternFetchMessage.e[this.remainElemsCount].x), c.length > 0 ? s.push(o) : n.push(o);
                return f = new r(h[0], s, n), u = f.getClosedList().concat(h)
            }, t.prototype.onSnapshotMessage = function (t) {
                return 0 === t.e.length || null == this.getNativeInfo() ? this.sendEvent([t]) : (this.patternFetchMessage = t, this.remainElemsCount = this.patternFetchMessage.e.length, void this.fetchPatterns(t, this.patternFetchMessage.e[--this.remainElemsCount]))
            }, t.prototype.fetchPatterns = function (t, e) {
                var n;
                return n = arguments, this.projectHash ? (this.xhr.open("POST", this.gtaHost() + "/projects/" + this.projectHash + "/xpaths"), this.xhr.setRequestHeader("Authorization", this.getNativeInfo().token), this.xhr.onreadystatechange = function (t) {
                    return function () {
                        return 4 === t.xhr.readyState && 200 === t.xhr.status ? t.onReceivePatterens(JSON.parse(t.xhr.response)) : "https://gta.growingio.com" !== t.gtaHost() ? t.sendEvent([t.patternFetchMessage]) : void 0
                    }
                }(this), this.xhr.send(JSON.stringify({
                    domain: t.d ? t.d : "",
                    page: t.p,
                    xpath: e.x
                })), console.log("fetching patterns for", e.x)) : this.fetchProjectHash(function (t) {
                    return function () {
                        return t.fetchPatterns.apply(t, n)
                    }
                }(this))
            }, t.prototype.onReceivePatterens = function (t) {
                var e, n, i, r;
                for (i = this.onCorrectXPath(0 === t.xpaths.length ? [this.patternFetchMessage.e[this.remainElemsCount].x] : t.xpaths.slice(1)), n = 0, e = i.length; n < e; n++) r = i[n], this.getNativeInfo().x && (r = this.getNativeInfo().x + "::" + r);
                return this.patternFetchMessage.e[this.remainElemsCount].patterns = i, this.remainElemsCount > 0 ? this.fetchPatterns(this.patternFetchMessage, this.patternFetchMessage.e[--this.remainElemsCount]) : this.sendEvent([this.patternFetchMessage])
            }, t.prototype.sendEvent = function (t) {
                if (this.findElem) return this.findElem = !1, _vds_hybrid.saveEvent(t)
            }, t.prototype.findHitElements = function (t, e) {
                return this.findHitElementRecr(document.body, t, e)
            }, t.prototype.cachedCircleable = function (t) {
                var e;
                return e = t[n], void 0 !== e ? e : t[n] = this.circleStyle.circleable(t)
            }, t.prototype.findHitElementRecr = function (t, e, n) {
                var i, r, a, s, o;
                for (i = [], o = t.children, s = 0, r = o.length; s < r; s++) a = o[s], this.cachedCircleable(a) && a !== this.hoverMaskView && this.hitTest(a, e, n) && i.push(a), a.children.length && (i = i.concat(this.findHitElementRecr(a, e, n)));
                return i
            }, t.prototype.findTopElement = function (t) {
                var e, n, i, r, a, s, o, h, l;
                for (a = ["absolute", "relative", "fixed"], o = t.pop(), h = getComputedStyle(o), l = isNaN(parseInt(h.zIndex)) || a.indexOf(h.position) === -1 ? 0 : parseInt(h.zIndex), s = 0, r = t.length; s < r; s++) e = t[s], e.parentElement === o.parentElement && (n = getComputedStyle(e), i = isNaN(parseInt(n.zIndex)) || a.indexOf(n.position) === -1 ? 0 : parseInt(n.zIndex), i > l && (o = e));
                return o
            }, t.prototype.hoverOn = function (t, e) {
                var n;
                if (t /= this.initScale, e /= this.initScale, this.curX !== t || this.curY !== e) return this.curX = t, this.curY = e, this.circling = !0, this.isMoving = !0, n = this.findHitElements(t, e), n.length ? (this.hoverElem = this.findTopElement(n), this.showMask(this.hoverElem)) : (this.hoverElem = null, this.hideMask()), this.invalidRectCache = !1
            }, t.prototype.findElementAtPoint = function (t) {
                var e;
                if (this.findElem = !0, this.hideMask(), e = new CustomEvent("snapshot"), e.seqid = t, e.snapshotCallback = function (t) {
                    return function () {
                        window._vds_bridge.hoverNodes(JSON.stringify(arguments[0]));
                        return t.onSnapshotMessage.apply(t, arguments)
                    }
                }(this), this.hoverElem) {
                    return this.hoverElem.dispatchEvent(e)
                }
            }, t.prototype.showMask = function (t) {
                var e, n;
                return this.hoverMaskView.parentNode || document.body.appendChild(this.hoverMaskView), e = t[i], n = this.hoverMaskView.style, n.left = e.left + "px", n.top = e.top + "px", n.width = e.width + "px", n.height = e.height + "px"
            }, t.prototype.hideMask = function () {
                var t;
                return this.invalidRectCache = !0, null != (t = this.hoverMaskView.parentNode) ? t.removeChild(this.hoverMaskView) : void 0
            }, t.prototype.hitTest = function (t, e, n) {
                var r;
                return r = t[i], r && !this.invalidRectCache || (r = t.getBoundingClientRect(), t[i] = r), r.left <= e && r.right >= e && r.top <= n && r.bottom >= n
            }, t.prototype.setTags = function (t) {
                var e, n, i, r;
                if (t) {
                    for (t = this.stripNativeXPath(this.filterElemTagsInPage(t, {
                        domain: location.host,
                        path: location.pathname,
                        query: m.query()
                    })), i = [], n = 0, e = t.length; n < e; n++) r = t[n], i.push(this.tagStore.add(r));
                    return i
                }
            }, t.prototype.stripNativeXPath = function (t) {
                return t.map(function (t) {
                    return t.filter.xpath && t.filter.xpath.indexOf("::") !== -1 && (t.filter.xpath = t.filter.xpath.split("::")[1]), t.attrs.xpath && t.attrs.xpath.indexOf("::") !== -1 && (t.attrs.xpath = t.attrs.xpath.split("::")[1]), t
                })
            }, t.prototype.filterElemTagsInPage = function (t, e) {
                var n, i, r, a;
                return n = e.domain, r = e.path, a = e.query, i = t.filter(function (t) {
                    var e, i, s;
                    return e = t.filter.domain, i = t.filter.path, s = t.filter.query, "elem" === t.eventType && (void 0 === e || (e.indexOf("*") === -1 ? e === n : n.match(e.replace("*", ".*")))) && (void 0 === i || "*" === i || "/*" === i || (i.indexOf("*") === -1 ? i === r : r.match(i.replace("*", ".*")))) && (void 0 === s || (s.indexOf("*") === -1 ? s === a : a.match(s.replace("*", ".*"))))
                }), i.sort(function (t, e) {
                    return void 0 === t.filter.path || "*" === t.filter.path || "*" === t.filter.path[t.filter.path.length - 1] ? 1 : void 0 === e.filter.path || "*" === e.filter.path || "*" === e.filter.path[e.filter.path.length - 1] ? -1 : t.filter.path > e.filter.path ? 1 : -1
                })
            }, t.prototype.showHeatMap = function (t) {
                this.nativeInfo = t, this.fetchHeatMapData()
            }, t.prototype.hideHeatMap = function () {
                return this.heatmap.turnOff()
            }, t.prototype.fetchProjectHash = function (t) {
                var e;
                if (null != (null != (e = this.getNativeInfo()) ? e.ai : void 0)) return this.xhr.open("GET", this.gtaHost() + "/mobile/project_id?" + Math.random()), this.xhr.setRequestHeader("accountId", this.getNativeInfo().ai), this.xhr.setRequestHeader("Authorization", this.getNativeInfo().token), this.xhr.onreadystatechange = function (e) {
                    return function () {
                        if (4 === e.xhr.readyState && 200 === e.xhr.status) return e.projectHash = JSON.parse(e.xhr.response).projectId, "function" == typeof t ? t() : void 0
                    }
                }(this), this.xhr.send()
            }, t.prototype.fetchHeatMapData = function () {
                var t, e, n;
                return this.projectHash ? (n = this.gtaHost() + "/projects/" + this.projectHash + "/heatmap/data", t = {
                    platform: "mobile",
                    domain: this.getNativeInfo().d + "::" + location.host,
                    metric: "clck",
                    path: this.getNativeInfo().p + "::" + m.path(),
                    withIndex: !1
                }, e = new Date, e.setHours(0), e.setMinutes(0), e.setSeconds(0), e.setMilliseconds(0), t.beginTime = e.getTime() - 6048e5, e.setHours(23), e.setMinutes(59), e.setSeconds(59), e.setMilliseconds(999), t.endTime = e.getTime(), this.xhr.open("POST", n), this.xhr.setRequestHeader("Authorization", this.getNativeInfo().token), this.xhr.send(JSON.stringify(t)), this.xhr.onreadystatechange = function (t) {
                    return function () {
                        var e;
                        if (4 === t.xhr.readyState && 200 === t.xhr.status) {
                            if (e = JSON.parse(t.xhr.response), !e.success) return;
                            return e.data = e.data.map(function (t) {
                                var e;
                                return (null != (e = t.x) ? e.indexOf(!0) : void 0) && (t.x = t.x.split("::").pop()), t
                            }), t.heatmap.turnOn(e)
                        }
                    }
                }(this)) : this.fetchProjectHash(function (t) {
                    return function () {
                        return t.fetchHeatMapData.apply(t, arguments)
                    }
                }(this))
            }, t
        }(), x = function (t) {
            var e, n;
            if (!t.circleStarted) return n = document.createElement("style"), n.innerHTML = ".growing-circle-tagged-cover { position: absolute; border: 1px solid #FFDD24; border-radius: 3px; background-color: rgba(255, 221, 36, 0.3); z-index: 999999; } div#vds-hybrid-mask-view { position: fixed; background-color: rgba(255, 72, 36, 0.3); border-radius: 3px; margin: 0; padding: 0; z-index: 99999; display: block; } .heatmap-canvas{cursor:pointer;position:fixed;left:0;top:0;background:rgba(0,0,0,0.2);display:none;z-index:99999999}.heatmap-tooltips{position:fixed;left:0;top:0;color:#fff;display:none;background:rgba(0,0,0,0.8);padding:10px;z-index:999999991;border-radius:2px;font-size:14px;text-align:left !important}.heatmap-message{position:fixed;left:0;width:100%;height:100%;top:0;background:rgba(0,0,0,0.6);display:none;z-index:999999991}.heatmap-message.heatmap-message-loading-with-shift-false::before,.heatmap-message.heatmap-message-loading-with-shift-true::before{position:relative;content:'';height:50px;width:50px;background-repeat:no-repeat;background-position:40%;top:48%;margin:0 auto;display:block;width:236px}.heatmap-message.heatmap-message-loading-with-shift-true{top:50%;left:50%;transform:translate(-50%, -50%);border-radius:2px;width:280px;height:100px;background:rgba(0,0,0,0.8)}.heatmap-message.heatmap-message-loading-with-shift-true::before{top:20%;background-position:50%}.heatmap-message.msg::before{position:relative;content:attr(data-msg);top:48%;padding:20px;background:rgba(0,0,0,0.7);color:#fff;border-radius:4px;margin:0 auto;display:block;width:236px;font-size:20px;text-align:center}", document.head.appendChild(n), e = new l, window._vds_hybrid_native_info ? e.nativeInfo = window._vds_hybrid_native_info : console.log("no native info input"), t.circleStarted = !0, t.helper = e, t.setTags = function () {
                return e.setTags.apply(e, arguments)
            }, t.hoverOn = function () {
                return e.hoverOn.apply(e, arguments)
            }, t.cancelHover = function () {
                return e.hideMask.apply(e, arguments)
            }, t.findElementAtPoint = function () {
                return e.findElementAtPoint.apply(e, arguments)
            }, t.showHeatMap = function () {
                return e.showHeatMap.apply(e, arguments)
            }, t.hideHeatMap = function () {
                return e.hideHeatMap.apply(e, arguments)
            }, t.setShowCircledTags = function (n) {
                return n ? t.isMoving ? void 0 : e.toggleEye.on() : e.toggleEye.off()
            }, console.log("start circle")
        }, (A = function () {
            return window._vds_hybrid ? x(window._vds_hybrid) : setTimeout(A, 1e3)
        })()
    }.call(this);