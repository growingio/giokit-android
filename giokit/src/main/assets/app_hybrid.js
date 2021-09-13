! function t(e, r, n) {
    function i(s, a) {
        if (!r[s]) {
            if (!e[s]) {
                var u = "function" == typeof require && require;
                if (!a && u) return u(s, !0);
                if (o) return o(s, !0);
                throw new Error("Cannot find module '" + s + "'")
            }
            var d = r[s] = {
                exports: {}
            };
            e[s][0].call(d.exports, function (t) {
                var r = e[s][1][t];
                return i(r ? r : t)
            }, d, d.exports, t, e, r, n)
        }
        return r[s].exports
    }
    for (var o = "function" == typeof require && require, s = 0; s < n.length; s++) i(n[s]);
    return i
}({
    1: [
        function (t, e, r) {
            var n, i, o, s, a, u, d, h = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                c = [].slice;
            d = t("../core/tools/utils"), n = t("../core/dimensions/app_dimension"), s = t("../core/dimensions/page_dimension"), o = t("../core/dimensions/evar_dimension"), a = t("../core/dimensions/people_dimension"), u = t("../core/dimensions/system_dimension"), i = function () {
                function t() {
                    this.callback = h(this.callback, this), this.defaultSamplingFunc = h(this.defaultSamplingFunc, this), this.options = d.assignObj({}, this.defaultOptions), this.started = !1, this._setup(), this.identityWindow(), this.initialCSFields(), this.initialPSFields(), this.commond_stack = []
                }
                return t.prototype.version = "2.1.29", t.prototype.circleHost = ("https:" === document.location.protocol ? "https://" : "http://") + "www.growingio.com", t.prototype.endpoint = "/events", t.prototype.defaultOptions = {
                    imp: !0,
                    hashtag: !1,
                    touch: !1,
                    bot: !0,
                    dataCollect: !1,
                    pathCaseSensitive: !0,
                    scheme: "https://",
                    host: "api.growingio.com"
                }, t.prototype._setup = function () {
                    return this.app = {}, this.people = {}, this.page = {}, this.evar = {}, this.system = {}
                }, t.prototype.pushToStack = function () {
                    var t;
                    return (t = this.commond_stack).push.apply(t, arguments)
                }, t.prototype.setTrackerScheme = function (t) {
                    var e;
                    return t && d.isString(t) && ("http" === (e = t.toLocaleLowerCase()) || "https" === e) ? this.options.scheme = t + "://" : void 0
                }, t.prototype.setZone = function (t) {
                    return t && "string" == typeof t && t.trim().length > 0 ? this.options.zone = t : void 0
                }, t.prototype.setTrackerHost = function (t) {
                    return t ? this.options.host = t : void 0
                }, t.prototype.setAccountId = function (t) {
                    return this.options.accountId = t
                }, t.prototype.setImp = function (t) {
                    return this.options.imp = t
                }, t.prototype.setSampling = function (t, e) {
                    return null == t && (t = 4), null == e && (e = this.defaultSamplingFunc), this.options.sampling = !0, this.options.sampling_ratio = t, this.options.sampling_func = e
                }, t.prototype.pathCaseSensitive = function (t) {
                    return this.options.pathCaseSensitive = !!t
                }, t.prototype.trackBot = function (t) {
                    return this.options.bot = t
                }, t.prototype.enableHT = function (t) {
                    return this.options.hashtag = t
                }, t.prototype.enableTouch = function (t) {
                    return this.options.touch = t
                }, t.prototype.dataCollect = function (t) {
                    return this.options.dataCollect = t
                }, t.prototype.defaultSamplingFunc = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.initialCSFields = function (t) {
                    var e, r, n;
                    for (n = [], e = r = 20; r >= 2; e = --r) n.push(this["setCS" + e] = function (t) {
                        return function (e) {
                            return function (e, r) {
                                var n;
                                return t.app.set((n = {}, n["" + e] = r, n))
                            }
                        }
                    }(this)(e));
                    return n
                }, t.prototype.initialPSFields = function () {
                    var t, e, r;
                    for (r = [], t = e = 20; e >= 1; t = --e) r.push(this["setPS" + t] = function (t) {
                        return function (e) {
                            return function (r) {
                                return t.page.setPS("PS" + e, r)
                            }
                        }
                    }(this)(t));
                    return r
                }, t.prototype.setPageGroup = function (t) {
                    return this.page.setName(t)
                }, t.prototype.track = function (t) {
                    var e;
                    return (e = this.system).track.apply(e, arguments)
                }, t.prototype.setAppId = function (t) {
                    return t ? this.options.appId = t : void 0
                }, t.prototype.getVisitUserId = function () {
                    return this.user.vid()
                }, t.prototype.config = function (t) {
                    var e, r, n;
                    for (e in t) n = t[e], "scheme" === e ? this.setTrackerScheme(n) : "sampling" === e ? this.setSampling.apply(this, n) : this.options[e] = n;
                    r = [];
                    for (e in this.options) r.push(window.vds[e] = this.options[e]);
                    return r
                }, t.prototype.trackPV = function (t, e) {
                    var r, n;
                    return t && null != (r = this.observer) && r.pageService.replaceProps(t), null != (n = this.observer) ? n.pageService.sendPV(null, e) : void 0
                }, t.prototype.sendPage = function () {
                    var t, e, r;
                    return t = {
                        sendVisit: !1,
                        addPreProps: !0,
                        useNewTime: !0
                    }, null != (e = this.observer) ? e.pageService.sendPV(t, null != (r = this.observer) ? r.sendPageCallback : void 0) : void 0
                }, t.prototype.init = function (t, e, r) {
                    var n, i, o, s;
                    if (t && d.isString(t) && 0 !== t.trim().length) {
                        for (d.isString(t) && this.setAccountId(t), d.isString(e) && null !== e.trim() ? this.setAppId(e) : d.isObject(e) && (r = e), window.vds = Object.assign({}, window.vds, {
                            origin: this.circleHost
                        }), this.config(r), o = ["app", "people", "page", "evar", "system"], n = 0, i = o.length; i > n; n++) s = o[n], this[s] = this.initDimensions(s);
                        return this.afterInitialize(), this.ready = !0
                    }
                }, t.prototype.afterInitialize = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.apply = function () {
                    var t, e, r, n, i;
                    if (n = arguments[0], t = 2 <= arguments.length ? c.call(arguments, 1) : [], d.isString(n)) try {
                        if (-1 === n.indexOf(".")) {
                            if (null != this[n]) return this[n].apply(this, t)
                        } else if (r = n.split("."), i = this[r[0]], i && null != i[r[1]]) return i[r[1]].apply(i, t)
                    } catch (o) {
                        return e = o, console.error(e)
                    }
                }, t.prototype.setOrigin = function (t) {
                    return t ? this.options.origin = t : void 0
                }, t.prototype.set = function (t, e) {
                    return this.options[t] = e
                }, t.prototype.setUid = function () {
                    return this.options.host ? d.sendRequest("https://" + this.options.host + "/touch") : void 0
                }, t.prototype.send = function () {
                    var t, e, r, n, i;
                    if (!this.started) try {
                        if (this.ready && this.verify()) return this.connect()
                    } catch (o) {
                        return e = o, console.error(e.message)
                    } finally {
                        for (this.started = !0, i = this.commond_stack, r = 0, n = i.length; n > r; r++) t = i[r], this.apply.apply(this, t)
                    }
                }, t.prototype.verify = function () {
                    return window._vds_hybrid ? !0 : -1 !== d.indexOf(["", "localhost", "127.0.0.1"], window.location.hostname) ? !1 : !0
                }, t.prototype.initSystemConfig = function (t) {
                    var e, r, n, i;
                    for (i = [], r = 0, n = t.length; n > r; r++) e = t[r], i.push(this.apply.apply(this, e));
                    return i
                }, t.prototype.identityWindow = function () {
                    return d.identityWindow()
                }, t.prototype.beforeConnect = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.connect = function () {
                    var t;
                    if (t = window.navigator.userAgent.toLowerCase(), t.match(/(bot|crawler|spider|scrapy|jiankongbao|slurp|transcoder|networkbench|oneapm)/i)) {
                        if (!this.options.bot) return;
                        window.vds.imp = !1, this.options.isBot = !0
                    }
                    return this.beforeConnect(), this.sender.connect({
                        isBot: !!this.options.isBot
                    })
                }, t.prototype.callback = function () {
                    return this.observer.observe()
                }, t.prototype.initDimensions = function (t) {
                    switch (t) {
                    case "app":
                        return new n(this, this.observer, this.sender, this.user);
                    case "people":
                        return new a(this, this.observer, this.sender, this.user);
                    case "page":
                        return new s(this, this.observer, this.sender, this.user);
                    case "evar":
                        return new o(this, this.observer, this.sender, this.user);
                    case "system":
                        return new u(this, this.observer, this.sender, this.user)
                    }
                    return dim
                }, t
            }(), e.exports = i
        }, {
            "../core/dimensions/app_dimension": 2,
            "../core/dimensions/evar_dimension": 4,
            "../core/dimensions/page_dimension": 5,
            "../core/dimensions/people_dimension": 6,
            "../core/dimensions/system_dimension": 7,
            "../core/tools/utils": 16
        }
    ],
    2: [
        function (t, e, r) {
            var n, i, o = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                s = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) a.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                a = {}.hasOwnProperty;
            i = t("./base_dimension"), n = function (t) {
                function e() {
                    this.defer_condition = o(this.defer_condition, this), this.set = o(this.set, this), e.__super__.constructor.apply(this, arguments)
                }
                return s(e, t), e.prototype.set = function (t) {
                    var e, r, n;
                    if (!t) return this.clearCache();
                    if (2 === arguments.length && this.utils.isString(arguments[0]) && 0 !== arguments[0].trim().length) e = {}, e[arguments[0]] = arguments[1], t = e;
                    else if (1 !== arguments.length || !this.utils.isObject(arguments[0]) || this.utils.isEmptyObject(arguments[0])) return;
                    this.app_cache = this.app_cache ? this.app_cache : t;
                    for (r in t) null != (n = this.app_cache) && (n[r] = t[r]);
                    return this.lastSetTimeout && clearTimeout(this.lastSetTimeout), this.lastSetTimeout = setTimeout(function (t) {
                        return function () {
                            return t.observer.setDeferWithPage({
                                "var": t.app_cache
                            }, t.defer_condition)
                        }
                    }(this), 0)
                }, e.prototype.defer_condition = function () {
                    return !!this.observer.pageLoaded && this.launcher.started
                }, e.prototype.clearCache = function () {
                    return delete this.app_cache
                }, e
            }(i), e.exports = n
        }, {
            "./base_dimension": 3
        }
    ],
    3: [
        function (t, e, r) {
            var n, i;
            i = t("../tools/utils"), n = function () {
                function t(t, e, r, n) {
                    this.launcher = t, this.observer = e, this.sender = r, this.gruser = n, this.send = this.sender.send, this.utils = i, this.bindPageChangeEvent()
                }
                return t.prototype.bindPageChangeEvent = function () {
                    return this.utils.bind(window, "popstate", this.pageChanged, !0), window.vds.hashtag ? this.utils.bind(window, "hashchange", this.pageChanged, !0) : void 0
                }, t.prototype.set = function () {
                    throw new Error("this a abstruct function")
                }, t.prototype.pageChange = function () {
                    return this.reset()
                }, t.prototype.reset = function () {}, t.prototype.buildMessage = function () {}, e.exports = t, t
            }()
        }, {
            "../tools/utils": 16
        }
    ],
    4: [
        function (t, e, r) {
            var n, i, o, s = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                a = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) u.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                u = {}.hasOwnProperty,
                d = [].slice;
            n = t("./base_dimension"), o = t("../tools/utils"), i = function (t) {
                function e() {
                    this._buildEvarMessage = s(this._buildEvarMessage, this), this.set = s(this.set, this), e.__super__.constructor.apply(this, arguments)
                }
                return a(e, t), e.prototype.set = function (t) {
                    var e, r;
                    if (!this.launcher.started) return this.launcher.pushToStack(["evar.set"].concat(d.call(arguments)));
                    if (1 === arguments.length && this.utils.isObject(arguments[0]) && !this.utils.isEmptyObject(arguments[0]));
                    else {
                        if (2 !== arguments.length || !this.utils.isString(arguments[0]) || !this.utils.isString(arguments[1]) || 0 === !arguments[0].trim().length) return;
                        e = {}, e[arguments[0]] = arguments[1], t = e
                    }
                    return r = this._buildEvarMessage(t), this.send([r], "evar")
                }, e.prototype._buildEvarMessage = function (t) {
                    var e, r, n, i;
                    return r = {
                        appId: window.vds.appId,
                        u: this.gruser.vid(),
                        s: this.gruser.sid(),
                        t: "evar",
                        tm: +Date.now(),
                        ptm: this.observer.pageLoaded,
                        p: this.utils.path()
                    }, i = o.vaildVar(t), i && (r["var"] = i), e = this.gruser.getCs1(), e && (r.cs1 = e), r.d = o.domain(), n = this.utils.query(), n.length > 0 && (r.q = n), r
                }, e
            }(n), e.exports = i
        }, {
            "../tools/utils": 16,
            "./base_dimension": 3
        }
    ],
    5: [
        function (t, e, r) {
            var n, i, o, s = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                a = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) u.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                u = {}.hasOwnProperty,
                d = [].slice;
            n = t("./base_dimension"), o = t("../tools/utils"), i = function (t) {
                function e() {
                    this.setPS = s(this.setPS, this), this.setName = s(this.setName, this), this.set = s(this.set, this), this.sendPvListener = s(this.sendPvListener, this), e.__super__.constructor.apply(this, arguments), this.properties = {}, this.pageService = this.observer.pageService, this.currentUrl = "", this.pvarCache = {}, this.pageService.registerSendPvListener(this.sendPvListener)
                }
                return a(e, t), e.prototype.sendPvListener = function (t) {
                    var e, r, n, i;
                    if (i = this.pvarCache[t.pagePath]) {
                        for (e = 0, r = i.length; r > e; e++) n = i[e], n.ptm = t.pageLoaded, n.tm = +Date.now(), this.send([n], "pvar");
                        return delete this.pvarCache[t.pagePath]
                    }
                }, e.prototype.set = function (t) {
                    var e, r, n;
                    if (!this.launcher.started) return this.launcher.pushToStack(["page.set"].concat(d.call(arguments)));
                    if (1 === arguments.length && this.utils.isObject(arguments[0]) && !this.utils.isEmptyObject(arguments[0]));
                    else {
                        if (2 !== arguments.length || !this.utils.isString(arguments[0]) || !this.utils.isString(arguments[1]) || 0 === !arguments[0].trim().length) return;
                        e = {}, e[arguments[0]] = arguments[1], t = e
                    }
                    window.location.href !== this.currentUrl && (this.currentUrl = window.location.href, this.properties = {});
                    for (r in t) n = t[r], this.properties[r] = n;
                    return this.lastSetTimeout && clearTimeout(this.lastSetTimeout), this.sendPvar()
                }, e.prototype.sendPvar = function () {
                    return this.utils.isEmptyObject(this.properties) ? void 0 : this.lastSetTimeout = setTimeout(function (t) {
                        return function () {
                            var e, r;
                            return e = t._buildMessage(t.properties), t.pageService.pagePath && e.p !== t.pageService.pagePath ? (r = t.pvarCache[e.p], null == r && (r = []), r.push(e), void(t.pvarCache[e.p] = r)) : t.send([e], "pvar")
                        }
                    }(this), 0)
                }, e.prototype.setName = function (t) {
                    return this.name = t, this.name && this.utils.isString(this.name) && 0 !== this.name.trim().length ? this.observer.setPageGroup(this.name) : void 0
                }, e.prototype.setPS = function (t, e) {
                    var r;
                    if (this.name && this.utils.isString(t) && 0 !== t.length) return this.pageService.reduceProps((r = {}, r["" + t] = e, r))
                }, e.prototype._buildMessage = function (t) {
                    var e, r, n, i;
                    return r = {
                        u: this.gruser.vid(),
                        s: this.gruser.sid(),
                        t: "pvar",
                        tm: +Date.now(),
                        ptm: this.observer.pageLoaded,
                        p: this.utils.path()
                    }, i = o.vaildVar(t), i && (r["var"] = i), e = this.gruser.getCs1(), e && (r.cs1 = e), window.vds.appId && (r.appId = window.vds.appId), r.d = o.domain(), n = this.utils.query(), n.length > 0 && (r.q = n), r
                }, e.prototype.reset = function () {
                    return this.clearCache
                }, e.prototype.clearCache = function () {
                    return this.properties = {}
                }, e
            }(n), e.exports = i
        }, {
            "../tools/utils": 16,
            "./base_dimension": 3
        }
    ],
    6: [
        function (t, e, r) {
            var n, i, o, s = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                a = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) u.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                u = {}.hasOwnProperty,
                d = [].slice;
            n = t("./base_dimension"), o = t("../tools/utils"), i = function (t) {
                function e() {
                    this.clearId = s(this.clearId, this), this.getId = s(this.getId, this), this.setId = s(this.setId, this), this._buildPeopleMessage = s(this._buildPeopleMessage, this), this.set = s(this.set, this), e.__super__.constructor.apply(this, arguments)
                }
                return a(e, t), e.prototype.set = function (t) {
                    var e, r;
                    if (!this.launcher.started) return this.launcher.pushToStack(["people.set"].concat(d.call(arguments)));
                    if (arguments.length > 1 && this.utils.isString(arguments[0]) && 0 !== arguments[0].trim().length) e = {}, e[arguments[0].trim()] = arguments[1], t = e;
                    else if (!this.utils.isObject(arguments[0]) || this.utils.isEmptyObject(arguments[0])) return;
                    return this.gruser.isHybrid || this.getId() ? (delete this.people_cache, r = this._buildPeopleMessage(t), this.send([r], "ppl")) : void(this.people_cache = t)
                }, e.prototype._buildPeopleMessage = function (t) {
                    var e, r, n, i;
                    return r = {
                        appId: window.vds.appId,
                        u: this.gruser.vid(),
                        t: "ppl",
                        tm: +Date.now(),
                        ptm: this.observer.pageLoaded,
                        p: this.utils.path(),
                        cs1: this.getId()
                    }, i = o.vaildVar(t), i && (r["var"] = i), e = this.gruser.getCs1(), e && (r.cs1 = e), r.d = o.domain(), n = this.utils.query(), n.length > 0 && (r.q = n), r
                }, e.prototype.setId = function (t) {
                    var e, r, n, i, o, s;
                    if (!this.launcher.started) return this.launcher.pushToStack(["people.setId"].concat(d.call(arguments)));
                    if (t && window.vds.accountId && this.getId() !== t && !this.gruser.isHybrid) return this.people_cache && this.set(this.people_cache), n = this.gruser.lastSessionId, r = this.gruser.sid(), e = s = !0, i = this.gruser.last_sent_sid_with_cs1(), this.getId() && this.getId() !== t && (r = this.gruser.guid(), s = !1), r === n && i === r && this.gruser.last_sent_cs1() !== t && (r = this.gruser.guid()), this.getId() || (s = !1), this.gruser.updateCS1(t, r), o = i && i === r, this.gruser.updateSessionId(r, o), setTimeout(function (t) {
                        return function () {
                            return t.observer.pageService.sendPV({
                                addPreProps: e,
                                useNewTime: s
                            }, t.observer.sendPageCallback), t.launcher.page.sendPvar()
                        }
                    }(this), 10)
                }, e.prototype.getId = function () {
                    return this.gruser.getCs1()
                }, e.prototype.clearId = function () {
                    return this.gruser.isHybrid ? void 0 : this.gruser.clearCs1()
                }, e
            }(n), e.exports = i
        }, {
            "../tools/utils": 16,
            "./base_dimension": 3
        }
    ],
    7: [
        function (t, e, r) {
            var n, i, o, s = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                a = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) u.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                u = {}.hasOwnProperty,
                d = [].slice;
            n = t("./base_dimension"), o = t("../tools/utils"), i = function (t) {
                function e() {
                    this._buildTrackMessage = s(this._buildTrackMessage, this), this.track = s(this.track, this), e.__super__.constructor.apply(this, arguments)
                }
                return a(e, t), e.prototype.track = function (t) {
                    var e, r, n, i, o, s, a, u;
                    if (t) {
                        if (!this.launcher.started) return this.launcher.pushToStack(["track"].concat(d.call(arguments)));
                        for (n = i = 0, o = arguments.length; o > i; n = ++i) e = arguments[n], this.utils.isFunction(e) ? r = e : this.utils.isObject(e) ? a = e : 0 === n ? t = e : s = e;
                        return u = this._buildTrackMessage(t, a, s), u && this.send([u], "cstm"), r ? r() : void 0
                    }
                }, e.prototype._buildTrackMessage = function (t, e, r) {
                    var n, i, s, a, u;
                    return null == e && (e = {}), o.isVaildIdentifier(t) ? (i = {
                        u: this.gruser.vid(),
                        s: this.gruser.sid(),
                        t: "cstm",
                        tm: +Date.now(),
                        ptm: this.observer.pageLoaded,
                        p: this.observer.currentPath,
                        n: t
                    }, window.vds.appId && (i.appId = window.vds.appId), a = o.vaildEventNumber(r), a && (i.num = a), u = o.vaildVar(e), u && (i["var"] = u), n = this.gruser.getCs1(), n && (i.cs1 = n), i.d = o.domain(), s = this.utils.query(), s.length > 0 && (i.q = s), i) : null
                }, e
            }(n), e.exports = i
        }, {
            "../tools/utils": 16,
            "./base_dimension": 3
        }
    ],
    8: [
        function (t, e, r) {
            var n, i, o, s, a, u;
            u = t("../tools/utils"), i = t("./tagging_node_info"), s = ["I", "SPAN", "EM", "svg"], o = ["TR", "LI", "DL"], a = ["A", "BUTTON"], n = function () {
                function t() {}
                return t.prototype.path = function (t, e) {
                    var r, n, o, s, d, h, c, l, p, f, g, v, m, w, y, b, _, C, N;
                    if (null == e && (e = !1), s = "", g = !1, n = {}, p = !1, l = !1, _ = void 0, d = 0, b = [], o = [], t === document) return {
                        isIgnore: !0
                    };
                    for (r = new i(t);
                        "body" !== r.name && "html" !== r.name && (n = {
                            x: r.path(),
                            h: r.href,
                            v: this.containerElemContent(r.node)
                        }, b.push(n.x), m = r.path(), r.isIgnore && (g = !0), !p && r.hasObj() && (p = !0, c = r.grObj), l || (r.hasIdx() ? (l = !0, h = r.grIdx) : (N = r.path(), (-1 !== N.indexOf("/dl") || -1 !== N.indexOf("/tr") || -1 !== N.indexOf("/li")) && (l = !0, h = this.index(r.node)))), y = r.node.parentNode, e && "" !== s && (-1 !== u.indexOf(a, r.node.tagName) || r.isContainer()) && (p ? n.obj = c : u.hasAttr(r.node.parentNode, "data-growing-info") && (n.obj = r.node.parentNode.getAttribute("data-growing-info")), n.x = d, o.push(n)), s = m + s, d++, y && y.tagName);) r = new i(y), 1 === d && (_ = r.node);
                    for (b.reverse(), f = 0, v = o.length; v > f; f++) w = o[f], w.x = b.slice(0, d - w.x).join(""), l && (w.idx = h), p && (w.obj = c);
                    return C = {
                        xpath: s,
                        containerMessage: o,
                        isIgnore: g
                    }, p && (C.obj = c), l && (C.idx = h), _ && (C.pnode = _), C
                }, t.prototype.index = function (t) {
                    var e, r, n, i, s, a, d;
                    for (s = t; s && "BODY" !== s.tagName && -1 === u.indexOf(o, s.tagName);) s = s.parentNode;
                    if (s)
                        for (a = s.parentNode, r = 1, d = a.childNodes, e = 0, n = d.length; n > e; e++)
                            if (i = d[e], i.tagName === s.tagName) {
                                if (u.hasAttr(i, "data-growing-idx") && (r = parseInt(i.getAttribute("data-growing-idx"))), i === s) return r;
                                r += 1
                            }
                }, t.prototype.isLeaf = function (t) {
                    var e, r, n, i;
                    if (t.hasChildNodes() && "svg" !== t.tagName)
                        for (i = t.childNodes, r = 0, n = i.length; n > r; r++)
                            if (e = i[r], 1 === e.nodeType) return !1;
                    return !0
                }, t.prototype.isParentOfLeaf = function (t) {
                    var e, r, n, i;
                    if (!t.childNodes) return !1;
                    if ("svg" === t.tagName) return !1;
                    for (i = t.childNodes, r = 0, n = i.length; n > r; r++)
                        if (e = i[r], !this.isLeaf(e)) return !1;
                    return !0
                }, t.prototype.depthInside = function (t, e, r) {
                    var n, i, o, s;
                    if (null == r && (r = 1), t.hasChildNodes()) {
                        if (r > e) return !1;
                        for (s = t.childNodes, n = 0, i = s.length; i > n; n++)
                            if (o = s[n], 1 === o.nodeType && !this.depthInside(o, e, r + 1)) return !1
                    }
                    return e >= r
                }, t.prototype.onlyContainsChildren = function (t, e) {
                    var r, n, i, o;
                    if (0 === !t.children.length) return !1;
                    for (o = t.children, n = 0, i = o.length; i > n; n++)
                        if (r = o[n], -1 === u.indexOf(e, r.tagName)) return !1;
                    return !0
                }, t.prototype.containerElemContent = function (t) {
                    var e;
                    if (u.hasAttr(t, "data-growing-title") && t.getAttribute("data-growing-title").length > 0) return t.getAttribute("data-growing-title");
                    if ("BUTTON" === t.tagName) {
                        if (t.name.length > 0) return t.name;
                        if (this.onlyContainsChildren(t, s) && null != t.textContent && (e = t.textContent.replace(/[\n \t]+/g, " ").trim(), e.length > 0 && e.length < 50)) return e
                    } else if ("A" === t.tagName) {
                        if (u.hasAttr(t, "data-growing-title") && t.getAttribute("data-growing-title").length > 0) return t.getAttribute("data-growing-title");
                        if (u.hasAttr(t, "title") && t.title.length > 0) return t.getAttribute("title");
                        if (this.onlyContainsChildren(t, s) && null != t.textContent) {
                            if (e = t.textContent.replace(/[\n \t]+/g, " ").trim(), e.length > 0) return e.length <= 50 ? e : e.slice(0, 50)
                        } else if (u.hasAttr(t, "href") && t.getAttribute("href").length > 0) return t.getAttribute("href")
                    } else if ("LABEL" === t.tagName) {
                        if (u.hasAttr(t, "data-growing-title") && t.getAttribute("data-growing-title").length > 0) return t.getAttribute("data-growing-title");
                        if (u.hasAttr(t, "title") && t.title.length > 0) return t.getAttribute("title");
                        if (null != t.textContent && (e = t.textContent.replace(/[\n \t]+/g, " ").trim(), e.length > 0 && e.length < 50)) return e
                    }
                }, t.prototype.isDOM = function (t) {
                    return "HTMLElement" in window ? t && t instanceof HTMLElement : !(!t || "object" != typeof t || 1 !== t.nodeType || !t.nodeName)
                }, t
            }(), e.exports = n
        }, {
            "../tools/utils": 16,
            "./tagging_node_info": 10
        }
    ],
    9: [
        function (t, e, r) {
            var n, i, o, s;
            i = t("../vendor/cookie"), o = t("../tools/guid"), s = t("../tools/utils"), n = function () {
                function t() {
                    this.userId = null, this.sessionId = null, this.lastSessionId = null, this.cookie = i, this.guid = o, this.utils = s, this.cookieDomain()
                }
                return t.prototype.isHybrid = !1, t.prototype.cookieDomain = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.vid = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.hasSid = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.sid = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.getCs1 = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.updateCS1 = function (t, e) {
                    throw null == e && (e = this.sessionId), new Error("this a inteface function")
                }, t.prototype.clearCs1 = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.updateSessionId = function (t, e, r) {
                    throw new Error("this a inteface function")
                }, t.prototype.isSendNewVisit = function () {
                    throw new Error("this a inteface function")
                }, t
            }(), e.exports = n
        }, {
            "../tools/guid": 14,
            "../tools/utils": 16,
            "../vendor/cookie": 17
        }
    ],
    10: [
        function (t, e, r) {
            var n, i, o, s;
            s = t("../tools/utils"), o = s.detectIE() || 0 / 0, i = /(^| )(clear|clearfix|active|hover|enabled|hidden|display|focus|disabled|ng-|growing-)[^\. ]*/g, n = function () {
                function t(t) {
                    var e, r, n;
                    this.node = t, this.name = t.tagName.toLowerCase(), s.hasAttr(t, "id") && null === t.getAttribute("id").match(/^[0-9]/) && (this.id = t.getAttribute("id")), this.isIgnore = s.hasAttr(t, "growing-ignore") || s.hasAttr(t, "data-growing-ignore"), s.hasAttr(t, "href") && (e = t.getAttribute("href"), e && 0 !== e.indexOf("javascript") && (this.href = s.normalizePath(e.slice(0, 320)))), s.hasAttr(t, "data-growing-info") && (this.grObj = t.getAttribute("data-growing-info")), s.hasAttr(t, "data-growing-idx") && (this.grIdx = parseInt(t.getAttribute("data-growing-idx"))), "input" === this.name && s.hasAttr(t, "name") && t.getAttribute("name") ? this.klass = [t.getAttribute("name")] : (r = null != (n = s.getKlassName(t, o)) ? n.replace(i, "").trim() : void 0, (null != r ? r.length : void 0) > 0 && (this.klass = r.split(/\s+/).sort()))
                }
                return t.prototype.path = function () {
                    var t, e, r, n, i;
                    if (n = "/" + this.name, null != this.id && (n += "#" + this.id), null != this.klass)
                        for (i = this.klass, t = 0, r = i.length; r > t; t++) e = i[t], n += "." + e;
                    return n
                }, t.prototype.hasObj = function () {
                    return null != this.grObj
                }, t.prototype.hasIdx = function () {
                    return null != this.grIdx
                }, t.prototype.isContainer = function () {
                    return s.hasAttr(this.node, "data-growing-container")
                }, t
            }(), e.exports = n
        }, {
            "../tools/utils": 16
        }
    ],
    11: [
        function (t, e, r) {
            var n, i, o, s, a, u, d = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                h = [].indexOf || function (t) {
                    for (var e = 0, r = this.length; r > e; e++)
                        if (e in this && this[e] === t) return e;
                    return -1
                };
            u = t("../tools/utils"), s = u.detectIE() || 0 / 0, a = u.detectIOS() || 0 / 0, 9 >= s && t("json2"), i = t("./messaging_observer"), o = t("../page_service"), n = function () {
                function t(t, e, r, n) {
                    this.sender = t, this.gruser = e, this.launcher = r, this.TreeMirror = n, this.setPSForActionMessage = d(this.setPSForActionMessage, this), this.pageChanged = d(this.pageChanged, this), this.sendPageCallback = d(this.sendPageCallback, this), this.content = {}, this.pageService = new o(this.sender, this.gruser, this.launcher)
                }
                var e, r, n, s, c, l, p, f, g, v, m, w, y, b, _;
                return r = null, y = 3, c = [], s = {}, _ = {
                    tspan: 1,
                    text: 1,
                    g: 1,
                    rect: 1,
                    path: 1,
                    defs: 1,
                    clipPath: 1,
                    desc: 1,
                    title: 1,
                    use: 1
                }, l = {}, n = {
                    adx: 0,
                    ady: 0
                }, b = null, e = ["TEXTAREA", "HTML", "BODY"], m = ["button", "submit"], w = ["A", "BUTTON", "INPUT", "IMG"], f = ["I", "SPAN", "EM", "svg"], v = ["A", "BUTTON"], g = v, p = ["radio", "checkbox"], t.prototype.sendPageCallback = function (t) {
                    return this.pageLoaded = t.pageLoaded, this.prevPageAttrs = u.assignObj({}, t.preProperties), this.lastActionTime = +Date.now()
                }, t.prototype.setPageGroup = function (t) {
                    return this.pageService.setPageGroup(t, function (e) {
                        return function (r) {
                            var n;
                            return e.sendPageCallback(r), null != (n = e.messagingObserver) ? n.sendPageLoad({
                                pg: t
                            }) : void 0
                        }
                    }(this))
                }, t.prototype.setDeferWithPage = function (t, e) {
                    return e ? this.pageService.setDeferSendWithPage(t, e, this.sendPageCallback) : void 0
                }, t.prototype.registerDomObserver = function () {
                    var t;
                    if (window.vds.imp) return null != r && r.disconnect(), t = {
                        initialize: function (t) {
                            return function (e) {
                                var r, n, i, o, s, a;
                                for (a = {
                                    u: t.gruser.vid(),
                                    s: t.gruser.sid(),
                                    t: "imp",
                                    tm: +Date.now(),
                                    ptm: t.pageLoaded,
                                    d: u.domain(),
                                    p: t.currentPath
                                }, t.currentQuery.length > 0 && (a.q = t.currentQuery), n = t.gruser.getCs1(), n && (a.cs1 = n), t.setPSForActionMessage(a), i = [], o = 0, s = e.length; s > o; o++) r = e[o], i = i.concat(t.getLeafNodes(r, e.length));
                                return a.e = i, t.sender.send([a])
                            }
                        }(this),
                        applyChanged: function (t) {
                            return function (e, n, i, o) {
                                var s, a, d, h, c, l;
                                if (n.length > 0 && !document.body.className.match(/\bvds-entrytext\b/)) {
                                    for (t.gruser.hasSid() || t.pageService.sendPV({
                                        sendVisit: !1,
                                        addPreProps: !0,
                                        useNewTime: !1
                                    }, t.sendPageCallback), c = {
                                        u: t.gruser.vid(),
                                        s: t.gruser.sid(),
                                        t: r.snapshoting ? "snap" : "imp",
                                        tm: +Date.now(),
                                        ptm: t.pageLoaded,
                                        d: u.domain(),
                                        p: t.currentPath
                                    }, s = t.gruser.getCs1(), s && (c.cs1 = s), t.currentQuery.length > 0 && (c.q = t.currentQuery), t.setPSForActionMessage(c), a = [], d = 0, h = n.length; h > d; d++) l = n[d], a = a.concat(t.getLeafNodes(l, l.length));
                                    if (c.e = a, a.length > 0) return t.sendPolicy(c)
                                }
                            }
                        }(this)
                    }, this.TreeMirror ? this.client = r = new this.TreeMirror.Client(document.body, t) : void 0
                }, t.prototype.sendPolicy = function (t) {
                    return c.push(t), 0 > y ? this.sendQueue() : (this.queueTimeout && clearTimeout(this.queueTimeout), this.queueTimeout = setTimeout(function (t) {
                        return function () {
                            return t.sendQueue()
                        }
                    }(this), 3e3), this.checkingBlance())
                }, t.prototype.deregisterDomObserver = function () {
                    return null != r ? r.disconnect() : void 0
                }, t.prototype.idle = function () {
                    return this.lastSentTime = this.lastSentTime || +Date.now(), this.lastSentTime && (+Date.now() - this.lastSentTime) / c.length > 300
                }, t.prototype.checkingBlance = function () {
                    return this.idle() ? y -= 1 : void 0
                }, t.prototype.sendQueue = function () {
                    return c.length > 0 && +Date.now() - this.lastActionTime < 12e4 && this.sender.send(c), this.queueTimeout = null, this.lastSentTime = +Date.now(), c = [], y = 3
                }, t.prototype.getLeafNodes = function (t, e) {
                    var r, n, i, o, s, a, d, h, c, l;
                    if (o = [], d = !0, t.leaf) 1 === t.nodeType && ((null != (h = t.attributes) ? h.href : void 0) || null != t.text || null != t.obj) && (a = this.nodeMessage(t, !0), e > 1 && (a.idx = t.idx), o.push(a));
                    else if (0 === t.childNodes.length && -1 === u.indexOf(w, t.tagName));
                    else {
                        for (c = t.childNodes, i = 0, s = c.length; s > i; i++) r = c[i], r.leaf || (d = !1), o = o.concat(this.getLeafNodes(r, t.childNodes.length));
                        (-1 !== u.indexOf(g, t.tagName) || d && ((null != (l = t.attributes) ? l.any : void 0) || t.text)) && (t.text || (n = t.dom, t.text = n ? this.info.containerElemContent(n) : u.parentOfLeafText(t)), a = this.nodeMessage(t, !1), o.push(a))
                    }
                    return o
                }, t.prototype.nodeMessage = function (t, e) {
                    var n, i, o, s;
                    return i = {
                        x: t.path
                    }, i.v = t.text, "A" === t.tagName && 0 === (null != (o = t.text) ? o.length : void 0) && (i.v = void 0), (null != (s = t.text) ? s.length : void 0) > 50 && (i.v = "A" === t.tagName ? t.text.slice(0, 50) : void 0), (n = t.attributes) && n.href && 0 !== n.href.indexOf("javascript") && (i.h = u.normalizePath(n.href.slice(0, 320)), delete t.attributes.href), t.idx && (i.idx = t.idx), t.obj && (i.obj = t.obj), this.appendMessageAttrs(r, i, t)
                }, t.prototype.appendMessageAttrs = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.appendEventAttrs = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.registerEventListener = function () {
                    var t, r, i, o, s, a, d, h, c;
                    a = {
                        click: "clck",
                        change: "chng",
                        submit: "sbmt",
                        snapshot: "snap"
                    }, t = "__mutation_summary_node_map_id__", r = function (t, e, r) {
                        return t.addEventListener ? t.addEventListener(e, r, !0) : t.attachEvent ? t.attachEvent("on" + e, r) : t["on" + e] = r
                    }, h = function (t) {
                        return n.adx = 0, n.ady = 0, b = +Date.now(), l = {
                            time: b,
                            target: t.target || t.srcElement,
                            x: t.touches[0].pageX,
                            y: t.touches[0].pageY
                        }
                    }, d = function (t) {
                        return n = {
                            adx: Math.abs(t.touches[0].pageX - l.x),
                            ady: Math.abs(t.touches[0].pageY - l.y)
                        }
                    }, c = function (t) {
                        var e, r;
                        return e = +Date.now() - l.time, 250 > e && n.adx < 20 && n.ady < 20 ? (r = {
                            target: l.target,
                            type: "click"
                        }, s(r)) : void 0
                    }, i = function () {
                        var t, e, n, i, o, s, a, u, l, p, f;
                        "ontouchstart" in window && (p = ["touchstart"], f = ["touchend", "touchcancel"], u = ["touchmove"]);
                        for (e = 0, o = p.length; o > e; e++) t = p[e], r(document, t, h);
                        for (n = 0, s = u.length; s > n; n++) t = u[n], r(document, t, d);
                        for (l = [], i = 0, a = f.length; a > i; i++) t = f[i], l.push(r(document, t, c));
                        return l
                    }, s = function (t) {
                        return function (r) {
                            var n, i, o, s, d, h, c;
                            if (!document.body.className.match(/\bvds-entrytext\b/)) {
                                for (t.lastActionTime = +Date.now(), c = r.target || r.srcElement; c && 1 === _[c.tagName] && c.parentNode;) c = c.parentNode;
                                if (n = t.info.path(c, !0), n.isIgnore) return;
                                if (h = c.tagName, "click" === r.type) {
                                    if (-1 !== u.indexOf(e, h)) return;
                                    if ("INPUT" === h && -1 === u.indexOf(m, c.type)) return;
                                    if (-1 === u.indexOf(w, h) && !t.info.depthInside(c, 4)) return
                                }
                                return t.gruser.hasSid() || t.pageService.sendPV({
                                    sendVisit: !0,
                                    addPreProps: !0,
                                    useNewTime: !0
                                }, t.sendPageCallback), s = {}, s.u = t.gruser.vid(), s.s = t.gruser.sid(), s.t = a[r.type], s.tm = +Date.now(), s.ptm = t.pageLoaded, s.d = u.domain(), s.p = t.currentPath, t.currentQuery.length > 0 && (s.q = t.currentQuery), i = t.gruser.getCs1(), i && (s.cs1 = i), t.setPSForActionMessage(s), d = "snapshot" === r.type, d && (r.type = "click"), o = t.compute(d ? "click" : r.type, c), t.appendElemAttrs(o, d, c, n), o.x = n.xpath, n.obj && (o.obj = n.obj), n.idx ? o.idx = n.idx : (-1 !== o.x.indexOf("/dl") || -1 !== o.x.indexOf("/tr") || -1 !== o.x.indexOf("/li")) && (o.idx = t.info.index(c)), s.e = 0 === n.containerMessage.length ? [o] : -1 !== u.indexOf(g, n.pnode.tagName) && t.info.onlyContainsChildren(n.pnode, f) ? n.containerMessage : [o].concat(n.containerMessage), d ? (s.isTrackingEditText = !("INPUT" !== h && "SELECT" !== h), t.snapshotCallback(r, s)) : t.sender.send([s])
                            }
                        }
                    }(this);
                    for (o in a) r(document, o, s);
                    return "boolean" == typeof window.hybridEnableTouch && window.hybridEnableTouch ? i() : void 0
                }, t.prototype.snapshotCallback = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.appendElemAttrs = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.compute = function (t, e) {
                    var r, n, i, o, s, a, d, h, c, l, v, w, y, b, _, C, N, S, E, x, I, O;
                    if (i = {}, I = e.tagName, "IMG" === I ? (null != (S = e.src) ? S.length : void 0) > 0 && -1 === e.src.indexOf("data:image") && (i.h = e.src) : u.hasAttr(e, "href") && (o = e.getAttribute("href"), o && 0 !== o.indexOf("javascript") && (i.h = u.normalizePath(o.slice(0, 320)))), u.hasAttr(e, "data-growing-title") && e.getAttribute("data-growing-title").length > 0) i.v = e.getAttribute("data-growing-title");
                    else if (u.hasAttr(e, "title") && e.getAttribute("title").length > 0) i.v = e.getAttribute("title");
                    else if ("click" === t)
                        if (this.info.isLeaf(e))
                            if ("IMG" === I) e.alt ? i.v = e.alt : i.h && (d = i.h.split("?")[0], a = d.split("/"), a.length > 0 && (i.v = a[a.length - 1]));
                            else if ("INPUT" === I && -1 !== u.indexOf(m, e.type)) i.v = e.value;
                    else if ("svg" === I)
                        for (E = e.childNodes, s = 0, _ = E.length; _ > s; s++) r = E[s], 1 === r.nodeType && "use" === r.tagName && u.hasAttr(r, "xlink:href") && (i.v = r.getAttribute("xlink:href"));
                    else O = "", null != e.textContent ? O = e.textContent.replace(/[\n \t]+/g, " ").trim() : null != e.innerText && (O = e.innerText.replace(/[\n \t]+/g, " ").trim()), O.length > 0 && (O.length < 50 ? i.v = O : "A" === I && (i.v = O.slice(0, 50)));
                    else -1 !== u.indexOf(g, I) ? (n = this.info.containerElemContent(e), this.info.isParentOfLeaf(e) && !this.info.onlyContainsChildren(e, f) ? (N = u.parentOfLeafText(e), N === n ? i.v = n : (i.v = N, i.obj = n || "")) : i.v = n) : this.info.isParentOfLeaf(e) && (i.v = u.parentOfLeafText(e));
                    else if ("change" === t) "TEXTAREA" !== I && ("INPUT" === I ? -1 !== u.indexOf(p, e.type) ? (i.v = e.value, l = e.parentNode, "LABEL" === l.tagName ? c = l : "BODY" !== l.tagName && (l = l.parentNode, "LABEL" === l.tagName ? c = l : e.id && (b = l.getElementsByTagName("label"), function () {
                        var t, r, n;
                        for (n = [], t = 0, r = b.length; r > t; t++) y = b[t], n.push(y["for"] === e.id);
                        return n
                    }() && (c = y))), null != c && (v = this.info.containerElemContent(c), (null != v ? v.length : void 0) > 0 && (i.v = v + " (" + e.checked + ")"))) : "password" !== e.type && (u.hasAttr(e, "growing-track") || u.hasAttr(e, "data-growing-track")) && (i.v = e.value) : "SELECT" === I && (i.v = e.value, 1 === e.selectedOptions.length && null != e.selectedOptions[0].label && (i.v = e.selectedOptions[0].label)));
                    else if ("submit" === t)
                        for (x = e.getElementsByTagName("input"), w = 0, C = x.length; C > w; w++) h = x[w], ("search" === h.type || "text" === h.type && ("q" === h.id || -1 !== h.id.indexOf("search") || -1 !== h.className.indexOf("search") || "q" === h.name || -1 !== h.name.indexOf("search"))) && (i.x = this.info.path(h).xpath,
                            i.v = h.value.trim());
                    return i
                }, t.prototype.registerCircleHandler = function () {
                    var t;
                    try {
                        if (!this.messagingObserver) return this.messagingObserver = new i, this.messagingObserver.sendPageLoad(this.prevPageAttrs)
                    } catch (e) {
                        t = e
                    }
                }, t.prototype.registerHistoryHandler = function () {
                    var t, e;
                    return t = window.history.pushState, e = window.history.replaceState, null != t && (window.history.pushState = function (e) {
                        return function () {
                            return e.prevUrl = window.location.toString(), t.apply(window.history, arguments), setTimeout(function () {
                                return e.pageChanged()
                            }, 0)
                        }
                    }(this)), null != e && (window.history.replaceState = function (t) {
                        return function () {
                            return t.prevUrl = window.location.toString(), e.apply(window.history, arguments), setTimeout(function () {
                                return t.pageChanged()
                            }, 0)
                        }
                    }(this)), null != t && (this.prevUrl = u.getDocumentReferrer(), "function" == typeof Object.defineProperty && Object.defineProperty(document, "referrer", {
                        get: function (t) {
                            return function () {
                                return t.prevUrl
                            }
                        }(this),
                        configurable: !0
                    }), u.bind(window, "popstate", this.pageChanged, !0)), window.vds.hashtag ? u.bind(window, "hashchange", this.pageChanged, !0) : void 0
                }, t.prototype.pageChanged = function () {
                    var t, e, r;
                    return t = u.path(), e = u.query(), this.currentPath !== t || this.currentQuery !== e ? (window.vds.hashtag && (this.prevUrl = window.location.protocol + "//" + window.location.host + this.currentPath, this.currentQuery && this.currentQuery.trim().length > 0 && (this.prevUrl += "?" + this.currentQuery)), this.currentPath = t, this.currentQuery = e, this.pageService.sendPV({
                        sendVisit: !0,
                        useNewTime: !0
                    }, this.sendPageCallback), null != (r = this.messagingObserver) ? r.sendPageLoad(this.prevPageAttrs) : void 0) : void 0
                }, t.prototype.domLoadedHandler = function (t) {
                    var e;
                    if (!this.domLoadedHandler.done) {
                        if (this.domLoadedHandler.done = !0, this.registerEventListener(), window.vds.imp = !1, null != this.TreeMirror && window.vds.imp) {
                            this.initServerImpSetting();
                            try {
                                u.detectIE() && null != (null != (e = window.angular) ? e.version : void 0) ? (window.angular.version.major > 1 || 1 === window.angular.version.major && window.angular.version.minor > 4 || 1 === window.angular.version.major && 4 === window.angular.version.minor && window.angular.version.dot > 0) && setTimeout(function (t) {
                                    return function () {
                                        return t.registerDomObserver()
                                    }
                                }(this), 1500) : setTimeout(function (t) {
                                    return function () {
                                        return t.registerDomObserver()
                                    }
                                }(this), 1500)
                            } catch (r) {
                                t = r, setTimeout(function (t) {
                                    return function () {
                                        return t.registerDomObserver()
                                    }
                                }(this), 1500)
                            }
                        }
                        return window.history.pushState && this.registerHistoryHandler(), this.sendRegisterCircleOption()
                    }
                }, t.prototype.initServerImpSetting = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.blind = function () {
                    throw new Error("this a inteface function")
                }, t.prototype.bindEvent = function () {
                    return u.bind(window, "message", function (t) {
                        return function (e) {
                            var r, n, i, o;
                            try {
                                if (r = e.data, i = r.mode || r.circleMode, "update-global-env" === i && (window.grCircleEnv = r.add_on_grSource), window.self !== window.top && r.ai !== window.vds.accountId) return;
                                switch (i) {
                                case "gr-register-SDK-circle":
                                    return t.registerCircleHandler(), u.spreadToInnerIframes(r);
                                case "gr-register-SDK-option":
                                    if (t.canPostRegisterMessage() && "grcw" === window.grSource.name && (o = window.grSource.id, h.call(r.tArr, o) < 0)) return r.tArr.push(window.grSource.id), parent.postMessage(r, "*");
                                    break;
                                case "page-load":
                                    if (window.self === window.top && null != r.add_on_grSource) return t.loadPlugin(r)
                                }
                            } catch (s) {
                                n = s
                            }
                        }
                    }(this))
                }, t.prototype.loadPlugin = function (t) {
                    var e, r, n, i, o, s, a, u;
                    for (this.pluginLoaded = !1, a = document.getElementsByTagName("script"), i = 0, o = a.length; o > i; i++)
                        if (n = a[i], u = n.getAttribute("src"), null != u && -1 !== u.indexOf("/outer-circle-plugin.js")) {
                            this.pluginLoaded = !0;
                            break
                        }
                    return this.pluginLoaded ? void 0 : (s = t.add_on_grSource.add_on_origin, window.grCircleEnv = t.add_on_grSource, e = document.createElement("script"), e.type = "text/javascript", e.charset = "UTF-8", e.src = s + "/assets/javascripts/outer-circle-plugin.js", document.head.appendChild(e), r = document.createElement("link"), r.rel = "stylesheet", r.href = s + "/assets/stylesheets/outer-circle-plugin.css", document.head.appendChild(r))
                }, t.prototype.sendRegisterCircleOption = function () {
                    var t;
                    if (this.canPostRegisterMessage()) return t = {
                        circleMode: "gr-register-SDK-option",
                        url: window.location.toString(),
                        ai: window.vds.accountId,
                        sna: window.grSource.name,
                        sid: window.grSource.id,
                        tna: "",
                        fsna: "sdk",
                        tArr: [window.grSource.id]
                    }, parent.postMessage(t, "*")
                }, t.prototype.canPostRegisterMessage = function () {
                    return parent && "function" == typeof parent.postMessage && window.self !== window.top && window.self !== window.parent
                }, t.prototype.observe = function () {
                    var t, e, r;
                    if (this.bindEvent(), this.blind()) return void this.sendRegisterCircleOption();
                    if (this.currentPath = u.path(), this.currentQuery = u.query(), this.pageService.sendPV({}, this.sendPageCallback), document.addEventListener) "interactive" === document.readyState || "complete" === document.readyState ? this.domLoadedHandler() : u.bind(document, "DOMContentLoaded", function (t) {
                        return function () {
                            return t.domLoadedHandler()
                        }
                    }(this));
                    else if (document.attachEvent) {
                        u.bind(document, "onreadystatechange", function (t) {
                            return function () {
                                return t.domLoadedHandler()
                            }
                        }(this)), r = !1;
                        try {
                            r = null === window.frameElement
                        } catch (n) {
                            e = n
                        }
                        document.documentElement.doScroll && r && (t = function (r) {
                            return function () {
                                try {
                                    document.documentElement.doScroll("left")
                                } catch (n) {
                                    return e = n, void setTimeout(t, 1)
                                }
                                return r.domLoadedHandler()
                            }
                        }(this))()
                    }
                    return u.bind(window, "load", function (t) {
                        return function () {
                            return t.domLoadedHandler()
                        }
                    }(this)), u.bind(window, "beforeunload", function (t) {
                        return function (e) {
                            var r, n;
                            if (t.queueTimeout && t.sendQueue(), n = +Date.now(), window.grWaitTime)
                                for (r = n + 50; r > n;) n = +Date.now()
                        }
                    }(this)), a && u.bind(window, "pagehide", function (t) {
                        return function (e) {
                            var r, n;
                            if (t.queueTimeout && t.sendQueue(), n = +Date.now(), window.grWaitTime)
                                for (r = n + 50; r > n;) n = +Date.now()
                        }
                    }(this)), u.bind(window, "unload", function (t) {
                        return function (t) {
                            var e;
                            if (window.grWaitTime)
                                for (; e < window.grWaitTime;) e = +Date.now()
                        }
                    }(this))
                }, t.prototype.setPSForActionMessage = function (t) {
                    return this.prevPageAttrs.pg ? t.pg = this.prevPageAttrs.pg : void 0
                }, t
            }(), e.exports = n
        }, {
            "../page_service": 13,
            "../tools/utils": 16,
            "./messaging_observer": 12,
            json2: 18
        }
    ],
    12: [
        function (t, e, r) {
            var n, i, o, s, a = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                u = [].indexOf || function (t) {
                    for (var e = 0, r = this.length; r > e; e++)
                        if (e in this && this[e] === t) return e;
                    return -1
                };
            s = t("../tools/utils"), o = ["circle-mode", "browse-mode"], i = ["load-plugin", "register-iframe", "circle-mode", "browse-mode", "page-load", "circle-load"], n = function () {
                function t() {
                    this.registerInnerIframe = a(this.registerInnerIframe, this), this.sendPageLoad = a(this.sendPageLoad, this), this.isSdkEvent = a(this.isSdkEvent, this), this.notifyInnerIframes = a(this.notifyInnerIframes, this), this.allowOrigin = window.vds.origin, this.bindEvents()
                }
                return t.prototype.bindEvents = function () {
                    return s.bind(window, "message", function (t) {
                        return function (e) {
                            var r, n, i, a, d, h, c, l;
                            if (n = e.data, n.ai === window.vds.accountId && (h = n.mode || n.circleMode, t.isSdkEvent(h) && (!n.fsna || "sdk" === n.fsna))) {
                                if ("grcw-inner-iframe" === n.sna) return void parent.postMessage(e.data, "*");
                                if (c = n.mode, u.call(o, c) >= 0) s.spreadToInnerIframes(n);
                                else if (n.tna !== window.grSource.name && n.tna && n.tid) {
                                    for (r = document.getElementsByTagName("iframe"), i = 0, d = r.length; d > i; i++) a = r[i], t.notifyInnerIframes(a, n);
                                    return
                                }
                                if (l = n.mode, u.call(o, l) >= 0 || n.tna === window.grSource.name || e.origin === window.vds.origin || -1 !== s.indexOf(["www.growingio.com", "growingio.com"], e.origin.split("://")[1])) switch (e.origin !== window.vds.origin && (window.vds.origin = e.origin), n.mode) {
                                case "load-plugin":
                                    return t.loadPlugin();
                                case "circle-mode":
                                    return t.startCircle();
                                case "browse-mode":
                                    return t.stopCircle()
                                }
                            }
                        }
                    }(this))
                }, t.prototype.notifyInnerIframes = function (t, e) {
                    var r;
                    return r = t.contentWindow, r ? r.postMessage(e, "*") : void 0
                }, t.prototype.isSdkEvent = function (t) {
                    return u.call(i, t) >= 0
                }, t.prototype.sendPageLoad = function (t) {
                    var e, r, n;
                    return null == t && (t = null), n = (null != (r = window.vds) ? r.pathCaseSensitive : void 0) ? window.location.toString() : window.location.toString().toLowerCase(), e = {
                        circleMode: "page-load",
                        url: n,
                        ai: window.vds.accountId,
                        ht: window.vds.hashtag,
                        sna: window.grSource.name,
                        sid: window.grSource.id,
                        tna: "",
                        fsna: "sdk",
                        add_on_grSource: {
                            grSource: window.grSource,
                            vds: window.vds,
                            add_on_origin: this.allowOrigin
                        }
                    }, (null != t ? t.pg : void 0) && (e.pa = t), parent.postMessage(e, "*"), this.pluginLoaded ? setTimeout(function () {
                        return "undefined" != typeof CircleEvents && null !== CircleEvents ? CircleEvents.publish("circle:load") : void 0
                    }, 200) : void 0
                }, t.prototype.registerInnerIframe = function (t) {
                    var e;
                    return null == t && (t = null), "grcw" !== window.grSource.name ? (e = {
                        circleMode: "register-iframe",
                        url: window.location.toString(),
                        ai: window.vds.accountId,
                        ht: window.vds.hashtag,
                        sna: window.grSource.name,
                        sid: window.grSource.id,
                        tna: "",
                        fsna: "sdk"
                    }, (null != t ? t.pg : void 0) && (e.pa = t), parent.postMessage(e, "*")) : void 0
                }, t.prototype.loadPlugin = function () {
                    var t, e, r, n, i, o, a, u;
                    for (this.pluginLoaded = !1, t = s.getCirclePluginFileName(window._gr_support_circle_pop_out), a = document.getElementsByTagName("script"), i = 0, o = a.length; o > i; i++)
                        if (n = a[i], u = n.getAttribute("src"), null != u && -1 !== u.indexOf("/" + t + ".js")) {
                            this.pluginLoaded = !0;
                            break
                        }
                    return this.pluginLoaded || (e = document.createElement("script"), e.type = "text/javascript", e.charset = "UTF-8", e.src = this.allowOrigin + "/assets/javascripts/" + t + ".js", document.head.appendChild(e), window._gr_support_circle_pop_out === !0) ? void 0 : (r = document.createElement("link"), r.rel = "stylesheet", r.href = this.allowOrigin + "/assets/stylesheets/circle-plugin.css", document.head.appendChild(r))
                }, t.prototype.startCircle = function () {
                    var t, e, r, n, i, o;
                    if (t = s.getCirclePluginFileName(window._gr_support_circle_pop_out), !this.pluginLoaded)
                        for (i = document.getElementsByTagName("script"), r = 0, n = i.length; n > r; r++)
                            if (e = i[r], o = e.getAttribute("src"), null != o && -1 !== o.indexOf("/" + t + ".js")) {
                                this.pluginLoaded = !0;
                                break
                            }
                    return this.pluginLoaded ? this.publishCircle() : void 0
                }, t.prototype.stopCircle = function () {
                    return "undefined" != typeof CircleEvents && null !== CircleEvents ? CircleEvents.publish("circle:stop") : void 0
                }, t.prototype.publishCircle = function () {
                    return "undefined" != typeof CircleEvents && null !== CircleEvents ? (CircleEvents.publish("circle:start"), this.registerInnerIframe()) : setTimeout(function (t) {
                        return function () {
                            return t.publishCircle()
                        }
                    }(this), 2e3)
                }, t
            }(), e.exports = n
        }, {
            "../tools/utils": 16
        }
    ],
    13: [
        function (t, e, r) {
            var n, i;
            i = t("./tools/utils"), n = function () {
                function t(t, e, r) {
                    this.sender = t, this.gruser = e, this.launcher = r, this.properties = {}, this.preProperties = {}, this.defaultOptions = {
                        sendVisit: !0,
                        addPreProps: !1,
                        useNewTime: !0
                    }
                }
                return t.prototype.replaceProps = function (t) {
                    return t ? this.properties = t : void 0
                }, t.prototype.reduceProps = function (t) {
                    var e, r;
                    if (t) {
                        r = [];
                        for (e in t) r.push(this.properties[e] = t[e]);
                        return r
                    }
                }, t.prototype.sendPV = function (t, e) {
                    var r, n;
                    if (null == e && (e = function () {
                        return {}
                    }), null != this.sender.sendVisitTimeout && this.sender.sendVisitTimeout > 0) return void setTimeout(function (r) {
                        return function () {
                            return r.sendPV(t, e)
                        }
                    }(this), this.sender.sendVisitTimeout);
                    if (this.messages = [], this.options = this.defaultOptions, t)
                        for (n in t) this.options[n] = t[n];
                    if (this.pagePath = i.path(), this.options.addPreProps)
                        for (n in this.preProperties) null == (r = this.properties)[n] && (r[n] = this.preProperties[n]);
                    return this.options.useNewTime && (this.pageLoaded = +Date.now()), null == this.pageLoaded && (this.pageLoaded = +Date.now()), this.options.sendVisit && this.gruser.isSendNewVisit() && this.messages.push(this._buildVisitMessage()), this.messages.push(this._buildPageMessage()), this.sender.send(this.messages, "pv"), this.preProperties = this.properties, this.properties = {}, this.sendPvListener(this), e(this)
                }, t.prototype.registerSendPvListener = function (t) {
                    return t ? this.sendPvListener = t : void 0
                }, t.prototype.setPageGroup = function (t, e) {
                    return t ? (this.reduceProps({
                        pg: t
                    }), this.pageLoaded && +Date.now() - this.pageLoaded < 4e3 && this.launcher.started ? setTimeout(function (t) {
                        return function () {
                            return t.sendPV({
                                sendVisit: !1,
                                addPreProps: !0,
                                useNewTime: !1
                            }, e)
                        }
                    }(this), 10) : void 0) : void 0
                }, t.prototype.setDeferSendWithPage = function (t, e, r) {
                    return e || t ? (this.reduceProps(t), e(this) ? this.sendPV({
                        addPreProps: !0,
                        useNewTime: !1
                    }, r) : void 0) : void 0
                }, t.prototype._buildPageMessage = function () {
                    var t, e, r;
                    if (r = {
                        u: this.gruser.vid(),
                        s: this.gruser.sid(),
                        tl: document.title.slice(0, 255),
                        t: "page",
                        tm: this.pageLoaded,
                        pt: window.location.protocol.substring(0, window.location.protocol.length - 1),
                        d: i.domain(),
                        p: this.pagePath,
                        rp: i.getDocumentReferrer()
                    }, i.query().length > 0 && (r.q = i.query()), t = this.gruser.getCs1(), t && (r.cs1 = t), window.gio && (r.appId = window.vds.appId), this.properties)
                        for (e in this.properties) r[e] = this.properties[e];
                    return r
                }, t.prototype._buildVisitMessage = function () {
                    var t, e, r;
                    return e = {
                        ai: window.vds.accountId,
                        av: window.vds.version,
                        b: "Web",
                        u: this.gruser.vid(),
                        s: this.gruser.sid(),
                        t: "vst",
                        tm: +Date.now(),
                        sh: window.screen.height,
                        sw: window.screen.width,
                        d: i.domain(),
                        p: this.pagePath,
                        rf: i.getDocumentReferrer(),
                        l: null != (r = navigator.language || navigator.browserLanguage) ? r.toLowerCase() : void 0
                    }, i.query().length > 0 && (e.q = i.query()), t = this.gruser.getCs1(), t && (e.cs1 = t), window.gio && (e.appId = window.vds.appId), e
                }, t
            }(), e.exports = n
        }, {
            "./tools/utils": 16
        }
    ],
    14: [
        function (t, e, r) {
            var n;
            n = function () {
                var t;
                return t = (new Date).getTime(), "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (e) {
                    var r, n;
                    return r = (t + 16 * Math.random()) % 16 | 0, t = Math.floor(t / 16), n = "x" === e ? r : 3 & r | 8, n.toString(16)
                })
            }, e.exports = n
        }, {}
    ],
    15: [
        function (t, e, r) {
            var n, i;
            i = t("cookie"), n = function () {
                function t() {}
                return t.get = function (t) {
                    var e;
                    try {
                        return window.localStorage ? this._readFromLocalStorage(t) : this._readFromCookie(t)
                    } catch (r) {
                        return e = r, null
                    }
                }, t.set = function (t, e, r) {
                    var n;
                    null == r && (r = 864e5);
                    try {
                        return window.localStorage ? this._setInLocalStorage(t, e, r) : this._setInCookie(t, e, r / 1e3)
                    } catch (i) {
                        return n = i, null
                    }
                }, t._readFromCookie = function (t) {
                    return i.getItem(t)
                }, t._setInCookie = function (t, e, r) {
                    return i.setItem(t, e, r, "/", window.location.hostname)
                }, t._readFromLocalStorage = function (t) {
                    var e, r;
                    return r = window.localStorage.getItem(t), r ? (e = JSON.parse(r), e.expiredAt && +e.expiredAt >= +Date.now() ? e.value : null) : null
                }, t._setInLocalStorage = function (t, e, r) {
                    return window.localStorage.setItem(t, JSON.stringify({
                        expiredAt: +Date.now() + r,
                        value: e
                    }))
                }, t.removeInCookie = function (t) {
                    return i.removeItem(t, "/", window.location.hostname)
                }, t.removeInLocalStorage = function (t) {
                    return window.localStorage.removeItem(t)
                }, t.removeItem = function (t) {
                    var e;
                    try {
                        return window.localStorage && this.removeInLocalStorage(t), this.removeInCookie(t)
                    } catch (r) {
                        return e = r, null
                    }
                }, t
            }(), window.GrLocalStore = n, e.exports = n
        }, {
            cookie: 17
        }
    ],
    16: [
        function (t, e, r) {
            var n, i, o, s;
            o = t("./guid"), n = t("./local_store"), i = t("../vendor/cookie"), s = {
                bind: function (t, e, r, n) {
                    return null == n && (n = !1), null != document.addEventListener ? t.addEventListener(e, r, n) : null != document.attachEvent ? t.attachEvent("on" + e, function () {
                        var e;
                        return e = window.event, e.currentTarget = t, e.target = e.srcElement, r.call(t, e)
                    }) : t["on" + e] = r
                }, getCirclePluginFileName: function (t) {
                    return null != t ? "inner-circle-plugin" : "circle-plugin"
                }, hasAttr: function (t, e) {
                    return t.hasAttribute ? t.hasAttribute(e) : !!t[e]
                }, path: function () {
                    var t, e, r;
                    return e = this.normalizePath(window.location.pathname), window.vds.hashtag && (t = window.location.hash, e += -1 !== t.indexOf("?") ? t.split("?")[0] : t), (null != (r = window.vds) ? r.pathCaseSensitive : void 0) ? e : e.toLowerCase()
                }, domain: function () {
                    return window._vds_ios ? !window.location.protocol || "file:" !== window.location.protocol && "applewebdata:" !== window.location.protocol ? window.location.host || window.vds.accountId : window.location.host ? "" : window.vds.accountId : window.location.host || window.vds.accountId
                }, domain_for_send: function () {
                    return window._vds_ios ? !window.location.protocol || "file:" !== window.location.protocol && "applewebdata:" !== window.location.protocol ? window.location.host : window.location.host ? "" : window.location.host : window.location.host
                }, isObject: function (t) {
                    return t && "object" == typeof t && t.constructor === Object
                }, isArray: function (t) {
                    return t && "object" == typeof t && t.constructor === Array
                }, isString: function (t) {
                    return t && "string" == typeof t && t.constructor === String
                }, isFunction: function (t) {
                    return t && "function" == typeof t
                }, isEmptyObject: function (t) {
                    var e, r;
                    for (e in t) return r = t[e], !1;
                    return !0
                }, isUndefined: function (t) {
                    return "undefined" == typeof t
                }, getKlassName: function (t, e) {
                    return null == e && (e = this.detectIE() || 0 / 0), 8 > e ? t.className : t.getAttribute("class")
                }, normalizePath: function (t) {
                    var e;
                    return e = t.length, e > 1 && "/" === t.charAt(e - 1) ? t.slice(0, e - 1) : t
                }, identityWindow: function () {
                    var t, e;
                    return t = o(), e = this.getWindowSourceName(), e && "grcw" === e || (e = "grcw-inner-iframe"), window.grSource = {
                        name: e,
                        id: t
                    }
                }, getWindowSourceName: function () {
                    var t, e;
                    return e = null != window.self.name ? window.self.name : window.name, e && window.nameStorage && (t = e.split(/[:?]/), 3 === t.length && "nameStorage" === t[0] && (e = t[1])), e
                }, query: function () {
                    var t, e;
                    return t = window.location.search, t = t.length > 1 && "?" === t.charAt(0) ? t.slice(1) : window.vds.hashtag && -1 !== window.location.hash.indexOf("?") ? window.location.hash.split("?")[1] : t, (null != (e = window.vds) ? e.pathCaseSensitive : void 0) ? t : t.toLowerCase()
                }, parentOfLeafText: function (t) {
                    var e, r, n, i, o, s;
                    if (n = "", !t.childNodes) return "";
                    for (s = t.childNodes, i = 0, o = s.length; o > i; i++) e = s[i], 3 === e.nodeType && (null != e.textContent ? r = e.textContent.trim() : null != e.data && (r = e.data.trim()), r.length > 0 && (n += r + " "));
                    return n = n.replace(/[\n \t]+/g, " ").trim(), n.length > 0 && n.length < 50 ? n : void 0
                }, indexOf: function (t, e) {
                    var r, n, i;
                    if (null != Array.prototype.indexOf) return t.indexOf(e);
                    for (n = t.length, r = -1; ++r < n;)
                        if (i = t[r], i === e) return r;
                    return -1
                }, detectIOS: function () {
                    return !!window.navigator.userAgent.match(/(iPad|iPhone|iPod)/g)
                }, detectIE: function () {
                    var t, e, r, n, i;
                    return i = window.navigator.userAgent, window.ActiveXObject && (e = i.indexOf("MSIE "), e > 0) ? parseInt(i.substring(e + 5, i.indexOf(".", e)), 10) : (n = i.indexOf("Trident/"), n > 0 ? (r = i.indexOf("rv:"), parseInt(i.substring(r + 3, i.indexOf(".", r)), 10)) : (t = i.indexOf("Edge/"), t > 0 ? parseInt(i.substring(t + 5, i.indexOf(".", t)), 10) : !1))
                }, hashCode: function (t) {
                    var e, r, n;
                    if (null == t && (t = ""), r = 0, null == t || "boolean" == typeof t || 0 === t.length) return r;
                    for (n = 0; n < t.length;) e = t.charCodeAt(n), r = (r << 5) - r + e, r &= r, n++;
                    return r
                }, sendRequest: function (t, e, r) {
                    var n;
                    if (window.XMLHttpRequest) {
                        if (n = new XMLHttpRequest, "withCredentials" in n) return n.open("GET", t, !0), n.withCredentials = !0, n.onreadystatechange = function () {
                            return 4 === n.readyState ? "function" == typeof e ? e(n) : void 0 : "function" == typeof r ? r(n) : void 0
                        }, n.send();
                        if ("undefined" != typeof XDomainRequest) return n = new XDomainRequest, "http:" === document.location.protocol && (t = t.replace("https://", "http://")), n.open("GET", t), n.onload = function (t) {
                            return function () {
                                return "function" == typeof e ? e(n) : void 0
                            }
                        }(this), n.onerror = function (t) {
                            return function (t) {
                                return "function" == typeof r ? r(n) : void 0
                            }
                        }(this), n.onprogress = function () {
                            return {}
                        }, n.ontimeout = function () {
                            return {}
                        }
                    }
                }, spreadToInnerIframes: function (t) {
                    var e, r, n, i, o;
                    for (r = document.getElementsByTagName("iframe"), o = [], n = 0, i = r.length; i > n; n++) e = r[n], o.push(this.spread(t, e));
                    return o
                }, spread: function (t, e) {
                    var r;
                    return r = null != e ? e.contentWindow : void 0, r ? r.postMessage(t, "*") : void 0
                }, assignObj: function (t, e) {
                    var r, n;
                    for (r in e) n = e[r], t[r] = n;
                    return t
                }, getDocumentReferrer: function (t) {
                    var e, r;
                    return r = t ? t : document.referrer, (null != (e = window.vds) ? e.pathCaseSensitive : void 0) ? r : r.toLowerCase()
                }, isVaildIdentifier: function (t) {
                    var e;
                    return null != t && "string" == typeof t && t.constructor === String && 0 < t.length && t.length <= 50 && (e = /^[a-zA-Z_][a-zA-Z0-9_:]*$/, e.test(t)) ? !0 : !1
                }, vaildEventNumber: function (t) {
                    return null == t || "number" != typeof t || isNaN(t) ? null : t % 1 !== 0 ? parseFloat(t.toFixed(2)) : t
                }, vaildVar: function (t) {
                    var e, r, n, i;
                    if (n = null, null != t)
                        for (r in t)
                            if (i = t[r], null == n && (n = {}), e = r.length <= 50 ? r : r.slice(0, 50), null != i && "string" == typeof i && i.constructor === String && (i = i.length <= 1e3 ? i : i.slice(0, 1e3)), n[e] = i, Object.getOwnPropertyNames(n).length >= 100) return n;
                    return n
                }
            }, e.exports = s
        }, {
            "../vendor/cookie": 17,
            "./guid": 14,
            "./local_store": 15
        }
    ],
    17: [
        function (t, e, r) {
            var n = /^(\.br\.|\.co\.|\.com\.|\.org\.|\.edu\.|\.net\.)/,
                i = {
                    getItem: function (t) {
                        return t ? decodeURIComponent(document.cookie.replace(new RegExp("(?:(?:^|.*;)\\s*" + encodeURIComponent(t).replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=\\s*([^;]*).*$)|^.*$"), "$1")) || null : null
                    }, setItem: function (t, e, r, n, i, o) {
                        if (!t || /^(?:expires|max\-age|path|domain|secure)$/i.test(t)) return !1;
                        var s = "";
                        if (r) switch (r.constructor) {
                        case Number:
                            s = r === 1 / 0 ? "; expires=Fri, 31 Dec 9999 23:59:59 GMT" : "; expires=" + new Date((new Date).getTime() + 1e3 * r).toUTCString();
                            break;
                        case String:
                            s = "; expires=" + r;
                            break;
                        case Date:
                            s = "; expires=" + r.toUTCString()
                        }
                        return document.cookie = encodeURIComponent(t) + "=" + encodeURIComponent(e) + s + (i ? "; domain=" + i : "") + (n ? "; path=" + n : "") + (o ? "; secure" : ""), !0
                    }, removeItem: function (t, e, r) {
                        return this.hasItem(t) ? (document.cookie = encodeURIComponent(t) + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT" + (r ? "; domain=" + r : "") + (e ? "; path=" + e : ""), !0) : !1
                    }, hasItem: function (t) {
                        return t ? new RegExp("(?:^|;\\s*)" + encodeURIComponent(t).replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=").test(document.cookie) : !1
                    }, keys: function () {
                        for (var t = document.cookie.replace(/((?:^|\s*;)[^\=]+)(?=;|$)|^\s*|\s*(?:\=[^;]*)?(?:\1|$)/g, "").split(/\s*(?:\=[^;]*)?;\s*/), e = t.length, r = 0; e > r; r++) t[r] = decodeURIComponent(t[r]);
                        return t
                    }
                },
                o = {
                    getItem: i.getItem,
                    removeItem: i.removeItem,
                    hasItem: i.hasItem,
                    keys: i.keys,
                    setItem: function (t, e, r, o, s, a) {
                        for (var u = 0; u < s.length; u++)
                            if (!n.test(s[u])) {
                                i.setItem(t, e, r, o, s[u], a);
                                break
                            }
                    }
                };
            e.exports = o
        }, {}
    ],
    18: [
        function (require, module, exports) {
            "object" != typeof JSON && (JSON = {}),
                function () {
                    "use strict";

                    function f(t) {
                        return 10 > t ? "0" + t : t
                    }

                    function this_value() {
                        return this.valueOf()
                    }

                    function quote(t) {
                        return rx_escapable.lastIndex = 0, rx_escapable.test(t) ? '"' + t.replace(rx_escapable, function (t) {
                            var e = meta[t];
                            return "string" == typeof e ? e : "\\u" + ("0000" + t.charCodeAt(0).toString(16)).slice(-4)
                        }) + '"' : '"' + t + '"'
                    }

                    function str(t, e) {
                        var r, n, i, o, s, a = gap,
                            u = e[t];
                        switch (u && "object" == typeof u && "function" == typeof u.toJSON && (u = u.toJSON(t)), "function" == typeof rep && (u = rep.call(e, t, u)), typeof u) {
                        case "string":
                            return quote(u);
                        case "number":
                            return isFinite(u) ? String(u) : "null";
                        case "boolean":
                        case "null":
                            return String(u);
                        case "object":
                            if (!u) return "null";
                            if (gap += indent, s = [], "[object Array]" === Object.prototype.toString.apply(u)) {
                                for (o = u.length, r = 0; o > r; r += 1) s[r] = str(r, u) || "null";
                                return i = 0 === s.length ? "[]" : gap ? "[\n" + gap + s.join(",\n" + gap) + "\n" + a + "]" : "[" + s.join(",") + "]", gap = a, i
                            }
                            if (rep && "object" == typeof rep)
                                for (o = rep.length, r = 0; o > r; r += 1) "string" == typeof rep[r] && (n = rep[r], i = str(n, u), i && s.push(quote(n) + (gap ? ": " : ":") + i));
                            else
                                for (n in u) Object.prototype.hasOwnProperty.call(u, n) && (i = str(n, u), i && s.push(quote(n) + (gap ? ": " : ":") + i));
                            return i = 0 === s.length ? "{}" : gap ? "{\n" + gap + s.join(",\n" + gap) + "\n" + a + "}" : "{" + s.join(",") + "}", gap = a, i
                        }
                    }
                    var rx_one = /^[\],:{}\s]*$/,
                        rx_two = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,
                        rx_three = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
                        rx_four = /(?:^|:|,)(?:\s*\[)+/g,
                        rx_escapable = /[\\\"\u0000-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
                        rx_dangerous = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
                    "function" != typeof Date.prototype.toJSON && (Date.prototype.toJSON = function () {
                        return isFinite(this.valueOf()) ? this.getUTCFullYear() + "-" + f(this.getUTCMonth() + 1) + "-" + f(this.getUTCDate()) + "T" + f(this.getUTCHours()) + ":" + f(this.getUTCMinutes()) + ":" + f(this.getUTCSeconds()) + "Z" : null
                    }, Boolean.prototype.toJSON = this_value, Number.prototype.toJSON = this_value, String.prototype.toJSON = this_value);
                    var gap, indent, meta, rep;
                    "function" != typeof JSON.stringify && (meta = {
                        "\b": "\\b",
                        "   ": "\\t",
                        "\n": "\\n",
                        "\f": "\\f",
                        "\r": "\\r",
                        '"': '\\"',
                        "\\": "\\\\"
                    }, JSON.stringify = function (t, e, r) {
                        var n;
                        if (gap = "", indent = "", "number" == typeof r)
                            for (n = 0; r > n; n += 1) indent += " ";
                        else "string" == typeof r && (indent = r); if (rep = e, e && "function" != typeof e && ("object" != typeof e || "number" != typeof e.length)) throw new Error("JSON.stringify");
                        return str("", {
                            "": t
                        })
                    }), "function" != typeof JSON.parse && (JSON.parse = function (text, reviver) {
                        function walk(t, e) {
                            var r, n, i = t[e];
                            if (i && "object" == typeof i)
                                for (r in i) Object.prototype.hasOwnProperty.call(i, r) && (n = walk(i, r), void 0 !== n ? i[r] = n : delete i[r]);
                            return reviver.call(t, e, i)
                        }
                        var j;
                        if (text = String(text), rx_dangerous.lastIndex = 0, rx_dangerous.test(text) && (text = text.replace(rx_dangerous, function (t) {
                            return "\\u" + ("0000" + t.charCodeAt(0).toString(16)).slice(-4)
                        })), rx_one.test(text.replace(rx_two, "@").replace(rx_three, "]").replace(rx_four, ""))) return j = eval("(" + text + ")"), "function" == typeof reviver ? walk({
                            "": j
                        }, "") : j;
                        throw new SyntaxError("JSON.parse")
                    })
                }()
        }, {}
    ],
    19: [
        function (t, e, r) {
            function n(t) {
                return '"' + t.replace(/"/, '\\"') + '"'
            }

            function i(t) {
                if ("string" != typeof t) throw Error("Invalid request opion. attribute must be a non-zero length string.");
                if (t = t.trim(), !t) throw Error("Invalid request opion. attribute must be a non-zero length string.");
                if (!t.match(b)) throw Error("Invalid request option. invalid attribute name: " + t);
                return t
            }

            function o(t) {
                if (!t.trim().length) throw Error("Invalid request option: elementAttributes must contain at least one attribute.");
                for (var e = {}, r = {}, n = t.split(/\s+/), o = 0; o < n.length; o++) {
                    var s = n[o];
                    if (s) {
                        var s = i(s),
                            a = s.toLowerCase();
                        if (e[a]) throw Error("Invalid request option: observing multiple case variations of the same attribute is not supported.");
                        r[s] = !0, e[a] = !0
                    }
                }
                return Object.keys(r)
            }

            function s(t) {
                var e = {};
                return t.forEach(function (t) {
                    t.qualifiers.forEach(function (t) {
                        e[t.attrName] = !0
                    })
                }), Object.keys(e)
            }
            var a = this.__extends || function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) e.hasOwnProperty(n) && (t[n] = e[n]);
                    r.prototype = e.prototype, t.prototype = new r
                },
                u = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
            u || (console.error("DOM Mutation Observers are required."), console.error("https://developer.mozilla.org/en-US/docs/DOM/MutationObserver"), u = function () {
                function t(t) {}
                return t.prototype.observe = function (t, e) {}, t.prototype.disconnect = function () {}, t.prototype.takeRecords = function () {}, t
            }());
            var d, h = function () {
                function t() {
                    this.nodes = [], this.values = []
                }
                return t.prototype.isIndex = function (t) {
                    return +t === t >>> 0
                }, t.prototype.nodeId = function (e) {
                    var r = e[t.ID_PROP];
                    return r || (r = e[t.ID_PROP] = t.nextId_++), r
                }, t.prototype.set = function (t, e) {
                    var r = this.nodeId(t);
                    this.nodes[r] = t, this.values[r] = e
                }, t.prototype.get = function (t) {
                    var e = this.nodeId(t);
                    return this.values[e]
                }, t.prototype.has = function (t) {
                    return this.nodeId(t) in this.nodes
                }, t.prototype.remove = function (t) {
                    var e = this.nodeId(t);
                    this.nodes[e] = void 0, this.values[e] = void 0
                }, t.prototype.keys = function () {
                    var t = [];
                    for (var e in this.nodes) this.isIndex(e) && t.push(this.nodes[e]);
                    return t
                }, t.ID_PROP = "__mutation_summary_node_map_id__", t.nextId_ = 1, t
            }();
            ! function (t) {
                t[t.STAYED_OUT = 0] = "STAYED_OUT", t[t.ENTERED = 1] = "ENTERED", t[t.STAYED_IN = 2] = "STAYED_IN", t[t.REPARENTED = 3] = "REPARENTED", t[t.REORDERED = 4] = "REORDERED", t[t.EXITED = 5] = "EXITED"
            }(d || (d = {}));
            var c = function () {
                    function t(t, e, r, n, i, o, s, a) {
                        void 0 === e && (e = !1), void 0 === r && (r = !1), void 0 === n && (n = !1), void 0 === i && (i = null), void 0 === o && (o = !1), void 0 === s && (s = null), void 0 === a && (a = null), this.node = t, this.childList = e, this.attributes = r, this.characterData = n, this.oldParentNode = i, this.added = o, this.attributeOldValues = s, this.characterDataOldValue = a, this.isCaseInsensitive = this.node.nodeType === Node.ELEMENT_NODE && this.node instanceof HTMLElement, this.isCaseInsensitive && (this.isCaseInsensitive = "undefined" != typeof HTMLDocument ? this.node.ownerDocument instanceof HTMLDocument : this.node.ownerDocument instanceof Document)
                    }
                    return t.prototype.getAttributeOldValue = function (t) {
                        return this.attributeOldValues ? (this.isCaseInsensitive && (t = t.toLowerCase()), this.attributeOldValues[t]) : void 0
                    }, t.prototype.getAttributeNamesMutated = function () {
                        var t = [];
                        if (!this.attributeOldValues) return t;
                        for (var e in this.attributeOldValues) t.push(e);
                        return t
                    }, t.prototype.attributeMutated = function (t, e) {
                        this.attributes = !0, this.attributeOldValues = this.attributeOldValues || {}, t in this.attributeOldValues || (this.attributeOldValues[t] = e)
                    }, t.prototype.characterDataMutated = function (t) {
                        this.characterData || (this.characterData = !0, this.characterDataOldValue = t)
                    }, t.prototype.removedFromParent = function (t) {
                        this.childList = !0, this.added || this.oldParentNode ? this.added = !1 : this.oldParentNode = t
                    }, t.prototype.insertedIntoParent = function () {
                        this.childList = !0, this.added = !0
                    }, t.prototype.getOldParent = function () {
                        if (this.childList) {
                            if (this.oldParentNode) return this.oldParentNode;
                            if (this.added) return null
                        }
                        return this.node.parentNode
                    }, t
                }(),
                l = function () {
                    function t() {
                        this.added = new h, this.removed = new h, this.maybeMoved = new h, this.oldPrevious = new h, this.moved = void 0
                    }
                    return t
                }(),
                p = function (t) {
                    function e(e, r) {
                        t.call(this), this.rootNode = e, this.reachableCache = void 0, this.wasReachableCache = void 0, this.anyParentsChanged = !1, this.anyAttributesChanged = !1, this.anyCharacterDataChanged = !1;
                        for (var n = 0; n < r.length; n++) {
                            var i = r[n];
                            switch (i.type) {
                            case "childList":
                                this.anyParentsChanged = !0;
                                for (var o = 0; o < i.removedNodes.length; o++) {
                                    var s = i.removedNodes[o];
                                    this.getChange(s).removedFromParent(i.target)
                                }
                                for (var o = 0; o < i.addedNodes.length; o++) {
                                    var s = i.addedNodes[o];
                                    this.getChange(s).insertedIntoParent()
                                }
                                break;
                            case "attributes":
                                this.anyAttributesChanged = !0;
                                var a = this.getChange(i.target);
                                a.attributeMutated(i.attributeName, i.oldValue);
                                break;
                            case "characterData":
                                this.anyCharacterDataChanged = !0;
                                var a = this.getChange(i.target);
                                a.characterDataMutated(i.oldValue)
                            }
                        }
                    }
                    return a(e, t), e.prototype.getChange = function (t) {
                        var e = this.get(t);
                        return e || (e = new c(t), this.set(t, e)), e
                    }, e.prototype.getOldParent = function (t) {
                        var e = this.get(t);
                        return e ? e.getOldParent() : t.parentNode
                    }, e.prototype.getIsReachable = function (t) {
                        if (t === this.rootNode) return !0;
                        if (!t) return !1;
                        this.reachableCache = this.reachableCache || new h;
                        var e = this.reachableCache.get(t);
                        return void 0 === e && (e = this.getIsReachable(t.parentNode), this.reachableCache.set(t, e)), e
                    }, e.prototype.getWasReachable = function (t) {
                        if (t === this.rootNode) return !0;
                        if (!t) return !1;
                        this.wasReachableCache = this.wasReachableCache || new h;
                        var e = this.wasReachableCache.get(t);
                        return void 0 === e && (e = this.getWasReachable(this.getOldParent(t)), this.wasReachableCache.set(t, e)), e
                    }, e.prototype.reachabilityChange = function (t) {
                        return this.getIsReachable(t) ? this.getWasReachable(t) ? d.STAYED_IN : d.ENTERED : this.getWasReachable(t) ? d.EXITED : d.STAYED_OUT
                    }, e
                }(h),
                f = function () {
                    function t(t, e, r, n, i) {
                        this.rootNode = t, this.mutations = e, this.selectors = r, this.calcReordered = n, this.calcOldPreviousSibling = i, this.treeChanges = new p(t, e), this.entered = [], this.exited = [], this.stayedIn = new h, this.visited = new h, this.childListChangeMap = void 0, this.characterDataOnly = void 0, this.matchCache = void 0, this.processMutations()
                    }
                    return t.prototype.processMutations = function () {
                        if (this.treeChanges.anyParentsChanged || this.treeChanges.anyAttributesChanged)
                            for (var t = this.treeChanges.keys(), e = 0; e < t.length; e++) this.visitNode(t[e], void 0)
                    }, t.prototype.visitNode = function (t, e) {
                        if (!this.visited.has(t)) {
                            this.visited.set(t, !0);
                            var r = this.treeChanges.get(t),
                                n = e;
                            if ((r && r.childList || void 0 == n) && (n = this.treeChanges.reachabilityChange(t)), n !== d.STAYED_OUT) {
                                if (this.matchabilityChange(t), n === d.ENTERED) this.entered.push(t);
                                else if (n === d.EXITED) this.exited.push(t), this.ensureHasOldPreviousSiblingIfNeeded(t);
                                else if (n === d.STAYED_IN) {
                                    var i = d.STAYED_IN;
                                    r && r.childList && (r.oldParentNode !== t.parentNode ? (i = d.REPARENTED, this.ensureHasOldPreviousSiblingIfNeeded(t)) : this.calcReordered && this.wasReordered(t) && (i = d.REORDERED)), this.stayedIn.set(t, i)
                                }
                                if (n !== d.STAYED_IN)
                                    for (var o = t.firstChild; o; o = o.nextSibling) this.visitNode(o, n)
                            }
                        }
                    }, t.prototype.ensureHasOldPreviousSiblingIfNeeded = function (t) {
                        if (this.calcOldPreviousSibling) {
                            this.processChildlistChanges();
                            var e = t.parentNode,
                                r = this.treeChanges.get(t);
                            r && r.oldParentNode && (e = r.oldParentNode);
                            var n = this.childListChangeMap.get(e);
                            n || (n = new l, this.childListChangeMap.set(e, n)), n.oldPrevious.has(t) || n.oldPrevious.set(t, t.previousSibling)
                        }
                    }, t.prototype.getChanged = function (t, e, r) {
                        this.selectors = e, this.characterDataOnly = r;
                        for (var n = 0; n < this.entered.length; n++) {
                            var i = this.entered[n],
                                o = this.matchabilityChange(i);
                            (o === d.ENTERED || o === d.STAYED_IN) && t.added.push(i)
                        }
                        for (var s = this.stayedIn.keys(), n = 0; n < s.length; n++) {
                            var i = s[n],
                                o = this.matchabilityChange(i);
                            if (o === d.ENTERED) t.added.push(i);
                            else if (o === d.EXITED) t.removed.push(i);
                            else if (o === d.STAYED_IN && (t.reparented || t.reordered)) {
                                var a = this.stayedIn.get(i);
                                t.reparented && a === d.REPARENTED ? t.reparented.push(i) : t.reordered && a === d.REORDERED && t.reordered.push(i)
                            }
                        }
                        for (var n = 0; n < this.exited.length; n++) {
                            var i = this.exited[n],
                                o = this.matchabilityChange(i);
                            (o === d.EXITED || o === d.STAYED_IN) && t.removed.push(i)
                        }
                    }, t.prototype.getOldParentNode = function (t) {
                        var e = this.treeChanges.get(t);
                        if (e && e.childList) return e.oldParentNode ? e.oldParentNode : null;
                        var r = this.treeChanges.reachabilityChange(t);
                        if (r === d.STAYED_OUT || r === d.ENTERED) throw Error("getOldParentNode requested on invalid node.");
                        return t.parentNode
                    }, t.prototype.getOldPreviousSibling = function (t) {
                        var e = t.parentNode,
                            r = this.treeChanges.get(t);
                        r && r.oldParentNode && (e = r.oldParentNode);
                        var n = this.childListChangeMap.get(e);
                        if (!n) throw Error("getOldPreviousSibling requested on invalid node.");
                        return n.oldPrevious.get(t)
                    }, t.prototype.getOldAttribute = function (t, e) {
                        var r = this.treeChanges.get(t);
                        if (!r || !r.attributes) throw Error("getOldAttribute requested on invalid node.");
                        var n = r.getAttributeOldValue(e);
                        if (void 0 === n) throw Error("getOldAttribute requested for unchanged attribute name.");
                        return n
                    }, t.prototype.attributeChangedNodes = function (t) {
                        if (!this.treeChanges.anyAttributesChanged) return {};
                        var e, r;
                        if (t) {
                            e = {}, r = {};
                            for (var n = 0; n < t.length; n++) {
                                var i = t[n];
                                e[i] = !0, r[i.toLowerCase()] = i
                            }
                        }
                        for (var o = {}, s = this.treeChanges.keys(), n = 0; n < s.length; n++) {
                            var a = s[n],
                                u = this.treeChanges.get(a);
                            if (u.attributes && d.STAYED_IN === this.treeChanges.reachabilityChange(a) && d.STAYED_IN === this.matchabilityChange(a))
                                for (var h = a, c = u.getAttributeNamesMutated(), l = 0; l < c.length; l++) {
                                    var i = c[l];
                                    if (!e || e[i] || u.isCaseInsensitive && r[i]) {
                                        var p = u.getAttributeOldValue(i);
                                        p !== h.getAttribute(i) && (r && u.isCaseInsensitive && (i = r[i]), o[i] = o[i] || [], o[i].push(h))
                                    }
                                }
                        }
                        return o
                    }, t.prototype.getOldCharacterData = function (t) {
                        var e = this.treeChanges.get(t);
                        if (!e || !e.characterData) throw Error("getOldCharacterData requested on invalid node.");
                        return e.characterDataOldValue
                    }, t.prototype.getCharacterDataChanged = function () {
                        if (!this.treeChanges.anyCharacterDataChanged) return [];
                        for (var t = this.treeChanges.keys(), e = [], r = 0; r < t.length; r++) {
                            var n = t[r];
                            if (d.STAYED_IN === this.treeChanges.reachabilityChange(n)) {
                                var i = this.treeChanges.get(n);
                                i.characterData && n.textContent != i.characterDataOldValue && e.push(n)
                            }
                        }
                        return e
                    }, t.prototype.computeMatchabilityChange = function (t, e) {
                        this.matchCache || (this.matchCache = []), this.matchCache[t.uid] || (this.matchCache[t.uid] = new h);
                        var r = this.matchCache[t.uid],
                            n = r.get(e);
                        return void 0 === n && (n = t.matchabilityChange(e, this.treeChanges.get(e)), r.set(e, n)), n
                    }, t.prototype.matchabilityChange = function (t) {
                        var e = this;
                        if (this.characterDataOnly) switch (t.nodeType) {
                        case Node.COMMENT_NODE:
                        case Node.TEXT_NODE:
                            return d.STAYED_IN;
                        default:
                            return d.STAYED_OUT
                        }
                        if (!this.selectors) return d.STAYED_IN;
                        if (t.nodeType !== Node.ELEMENT_NODE) return d.STAYED_OUT;
                        for (var r = t, n = this.selectors.map(function (t) {
                            return e.computeMatchabilityChange(t, r)
                        }), i = d.STAYED_OUT, o = 0; i !== d.STAYED_IN && o < n.length;) {
                            switch (n[o]) {
                            case d.STAYED_IN:
                                i = d.STAYED_IN;
                                break;
                            case d.ENTERED:
                                i = i === d.EXITED ? d.STAYED_IN : d.ENTERED;
                                break;
                            case d.EXITED:
                                i = i === d.ENTERED ? d.STAYED_IN : d.EXITED
                            }
                            o++
                        }
                        return i
                    }, t.prototype.getChildlistChange = function (t) {
                        var e = this.childListChangeMap.get(t);
                        return e || (e = new l, this.childListChangeMap.set(t, e)), e
                    }, t.prototype.processChildlistChanges = function () {
                        function t(t, e) {
                            !t || n.oldPrevious.has(t) || n.added.has(t) || n.maybeMoved.has(t) || e && (n.added.has(e) || n.maybeMoved.has(e)) || n.oldPrevious.set(t, e)
                        }
                        if (!this.childListChangeMap) {
                            this.childListChangeMap = new h;
                            for (var e = 0; e < this.mutations.length; e++) {
                                var r = this.mutations[e];
                                if ("childList" == r.type && (this.treeChanges.reachabilityChange(r.target) === d.STAYED_IN || this.calcOldPreviousSibling)) {
                                    for (var n = this.getChildlistChange(r.target), i = r.previousSibling, o = 0; o < r.removedNodes.length; o++) {
                                        var s = r.removedNodes[o];
                                        t(s, i), n.added.has(s) ? n.added.remove(s) : (n.removed.set(s, !0), n.maybeMoved.remove(s)), i = s
                                    }
                                    t(r.nextSibling, i);
                                    for (var o = 0; o < r.addedNodes.length; o++) {
                                        var s = r.addedNodes[o];
                                        n.removed.has(s) ? (n.removed.remove(s), n.maybeMoved.set(s, !0)) : n.added.set(s, !0)
                                    }
                                }
                            }
                        }
                    }, t.prototype.wasReordered = function (t) {
                        function e(t) {
                            if (!t) return !1;
                            if (!s.maybeMoved.has(t)) return !1;
                            var e = s.moved.get(t);
                            return void 0 !== e ? e : (a.has(t) ? e = !0 : (a.set(t, !0), e = n(t) !== r(t)), a.has(t) ? (a.remove(t), s.moved.set(t, e)) : e = s.moved.get(t), e)
                        }

                        function r(t) {
                            var n = u.get(t);
                            if (void 0 !== n) return n;
                            for (n = s.oldPrevious.get(t); n && (s.removed.has(n) || e(n));) n = r(n);
                            return void 0 === n && (n = t.previousSibling), u.set(t, n), n
                        }

                        function n(t) {
                            if (d.has(t)) return d.get(t);
                            for (var r = t.previousSibling; r && (s.added.has(r) || e(r));) r = r.previousSibling;
                            return d.set(t, r), r
                        }
                        if (!this.treeChanges.anyParentsChanged) return !1;
                        this.processChildlistChanges();
                        var i = t.parentNode,
                            o = this.treeChanges.get(t);
                        o && o.oldParentNode && (i = o.oldParentNode);
                        var s = this.childListChangeMap.get(i);
                        if (!s) return !1;
                        if (s.moved) return s.moved.get(t);
                        s.moved = new h;
                        var a = new h,
                            u = new h,
                            d = new h;
                        return s.maybeMoved.keys().forEach(e), s.moved.get(t)
                    }, t
                }(),
                g = function () {
                    function t(t, e) {
                        var r = this;
                        if (this.projection = t, this.added = [], this.removed = [], this.reparented = e.all || e.element || e.characterData ? [] : void 0, this.reordered = e.all ? [] : void 0, t.getChanged(this, e.elementFilter, e.characterData), e.all || e.attribute || e.attributeList) {
                            var n = e.attribute ? [e.attribute] : e.attributeList,
                                i = t.attributeChangedNodes(n);
                            e.attribute ? this.valueChanged = i[e.attribute] || [] : (this.attributeChanged = i, e.attributeList && e.attributeList.forEach(function (t) {
                                r.attributeChanged.hasOwnProperty(t) || (r.attributeChanged[t] = [])
                            }))
                        }
                        if (e.all || e.characterData) {
                            var o = t.getCharacterDataChanged();
                            e.characterData ? this.valueChanged = o : this.characterDataChanged = o
                        }
                        this.reordered && (this.getOldPreviousSibling = t.getOldPreviousSibling.bind(t))
                    }
                    return t.prototype.getOldParentNode = function (t) {
                        return this.projection.getOldParentNode(t)
                    }, t.prototype.getOldAttribute = function (t, e) {
                        return this.projection.getOldAttribute(t, e)
                    }, t.prototype.getOldCharacterData = function (t) {
                        return this.projection.getOldCharacterData(t)
                    }, t.prototype.getOldPreviousSibling = function (t) {
                        return this.projection.getOldPreviousSibling(t)
                    }, t
                }(),
                v = /[a-zA-Z_]+/,
                m = /[a-zA-Z0-9_\-]+/,
                w = function () {
                    function t() {}
                    return t.prototype.matches = function (t) {
                        if (null === t) return !1;
                        if (void 0 === this.attrValue) return !0;
                        if (!this.contains) return this.attrValue == t;
                        for (var e = t.split(" "), r = 0; r < e.length; r++)
                            if (this.attrValue === e[r]) return !0;
                        return !1
                    }, t.prototype.toString = function () {
                        return "class" === this.attrName && this.contains ? "." + this.attrValue : "id" !== this.attrName || this.contains ? this.contains ? "[" + this.attrName + "~=" + n(this.attrValue) + "]" : "attrValue" in this ? "[" + this.attrName + "=" + n(this.attrValue) + "]" : "[" + this.attrName + "]" : "#" + this.attrValue
                    }, t
                }(),
                y = function () {
                    function t() {
                        this.uid = t.nextUid++, this.qualifiers = []
                    }
                    return Object.defineProperty(t.prototype, "caseInsensitiveTagName", {
                        get: function () {
                            return this.tagName.toUpperCase()
                        }, enumerable: !0,
                        configurable: !0
                    }), Object.defineProperty(t.prototype, "selectorString", {
                        get: function () {
                            return this.tagName + this.qualifiers.join("")
                        }, enumerable: !0,
                        configurable: !0
                    }), t.prototype.isMatching = function (e) {
                        var r = e[t.matchesSelector];
                        if (r) return e[t.matchesSelector](this.selectorString);
                        var n = e,
                            i = n.parentNode || n.document,
                            o = -1;
                        if (null === i || "undefined" == typeof i) return !1;
                        for (var s = i.querySelectorAll(selector); s[++o] && s[o] != n;);
                        return !!s[o]
                    }, t.prototype.wasMatching = function (t, e, r) {
                        if (!e || !e.attributes) return r;
                        var n = e.isCaseInsensitive ? this.caseInsensitiveTagName : this.tagName;
                        if ("*" !== n && n !== t.tagName) return !1;
                        for (var i = [], o = !1, s = 0; s < this.qualifiers.length; s++) {
                            var a = this.qualifiers[s],
                                u = e.getAttributeOldValue(a.attrName);
                            i.push(u), o = o || void 0 !== u
                        }
                        if (!o) return r;
                        for (var s = 0; s < this.qualifiers.length; s++) {
                            var a = this.qualifiers[s],
                                u = i[s];
                            if (void 0 === u && (u = t.getAttribute(a.attrName)), !a.matches(u)) return !1
                        }
                        return !0
                    }, t.prototype.matchabilityChange = function (t, e) {
                        var r = this.isMatching(t);
                        return r ? this.wasMatching(t, e, r) ? d.STAYED_IN : d.ENTERED : this.wasMatching(t, e, r) ? d.EXITED : d.STAYED_OUT
                    }, t.parseSelectors = function (e) {
                        function r() {
                            i && (o && (i.qualifiers.push(o), o = void 0), a.push(i)), i = new t
                        }

                        function n() {
                            o && i.qualifiers.push(o), o = new w
                        }
                        for (var i, o, s, a = [], u = /\s/, d = "Invalid or unsupported selector syntax.", h = 1, c = 2, l = 3, p = 4, f = 5, g = 6, y = 7, b = 8, _ = 9, C = 10, N = 11, S = 12, E = 13, x = 14, I = h, O = 0; O < e.length;) {
                            var A = e[O++];
                            switch (I) {
                            case h:
                                if (A.match(v)) {
                                    r(), i.tagName = A, I = c;
                                    break
                                }
                                if ("*" == A) {
                                    r(), i.tagName = "*", I = l;
                                    break
                                }
                                if ("." == A) {
                                    r(), n(), i.tagName = "*", o.attrName = "class", o.contains = !0, I = p;
                                    break
                                }
                                if ("#" == A) {
                                    r(), n(), i.tagName = "*", o.attrName = "id", I = p;
                                    break
                                }
                                if ("[" == A) {
                                    r(), n(), i.tagName = "*", o.attrName = "", I = g;
                                    break
                                }
                                if (A.match(u)) break;
                                throw Error(d);
                            case c:
                                if (A.match(m)) {
                                    i.tagName += A;
                                    break
                                }
                                if ("." == A) {
                                    n(), o.attrName = "class", o.contains = !0, I = p;
                                    break
                                }
                                if ("#" == A) {
                                    n(), o.attrName = "id", I = p;
                                    break
                                }
                                if ("[" == A) {
                                    n(), o.attrName = "", I = g;
                                    break
                                }
                                if (A.match(u)) {
                                    I = x;
                                    break
                                }
                                if ("," == A) {
                                    I = h;
                                    break
                                }
                                throw Error(d);
                            case l:
                                if ("." == A) {
                                    n(), o.attrName = "class", o.contains = !0, I = p;
                                    break
                                }
                                if ("#" == A) {
                                    n(), o.attrName = "id", I = p;
                                    break
                                }
                                if ("[" == A) {
                                    n(), o.attrName = "", I = g;
                                    break
                                }
                                if (A.match(u)) {
                                    I = x;
                                    break
                                }
                                if ("," == A) {
                                    I = h;
                                    break
                                }
                                throw Error(d);
                            case p:
                                if (A.match(v)) {
                                    o.attrValue = A, I = f;
                                    break
                                }
                                throw Error(d);
                            case f:
                                if (A.match(m)) {
                                    o.attrValue += A;
                                    break
                                }
                                if ("." == A) {
                                    n(), o.attrName = "class", o.contains = !0, I = p;
                                    break
                                }
                                if ("#" == A) {
                                    n(), o.attrName = "id", I = p;
                                    break
                                }
                                if ("[" == A) {
                                    n(), I = g;
                                    break
                                }
                                if (A.match(u)) {
                                    I = x;
                                    break
                                }
                                if ("," == A) {
                                    I = h;
                                    break
                                }
                                throw Error(d);
                            case g:
                                if (A.match(v)) {
                                    o.attrName = A, I = y;
                                    break
                                }
                                if (A.match(u)) break;
                                throw Error(d);
                            case y:
                                if (A.match(m)) {
                                    o.attrName += A;
                                    break
                                }
                                if (A.match(u)) {
                                    I = b;
                                    break
                                }
                                if ("~" == A) {
                                    o.contains = !0, I = _;
                                    break
                                }
                                if ("=" == A) {
                                    o.attrValue = "", I = N;
                                    break
                                }
                                if ("]" == A) {
                                    I = l;
                                    break
                                }
                                throw Error(d);
                            case b:
                                if ("~" == A) {
                                    o.contains = !0, I = _;
                                    break
                                }
                                if ("=" == A) {
                                    o.attrValue = "", I = N;
                                    break
                                }
                                if ("]" == A) {
                                    I = l;
                                    break
                                }
                                if (A.match(u)) break;
                                throw Error(d);
                            case _:
                                if ("=" == A) {
                                    o.attrValue = "", I = N;
                                    break
                                }
                                throw Error(d);
                            case C:
                                if ("]" == A) {
                                    I = l;
                                    break
                                }
                                if (A.match(u)) break;
                                throw Error(d);
                            case N:
                                if (A.match(u)) break;
                                if ('"' == A || "'" == A) {
                                    s = A, I = E;
                                    break
                                }
                                o.attrValue += A, I = S;
                                break;
                            case S:
                                if (A.match(u)) {
                                    I = C;
                                    break
                                }
                                if ("]" == A) {
                                    I = l;
                                    break
                                }
                                if ("'" == A || '"' == A) throw Error(d);
                                o.attrValue += A;
                                break;
                            case E:
                                if (A == s) {
                                    I = C;
                                    break
                                }
                                o.attrValue += A;
                                break;
                            case x:
                                if (A.match(u)) break;
                                if ("," == A) {
                                    I = h;
                                    break
                                }
                                throw Error(d)
                            }
                        }
                        switch (I) {
                        case h:
                        case c:
                        case l:
                        case f:
                        case x:
                            r();
                            break;
                        default:
                            throw Error(d)
                        }
                        if (!a.length) throw Error(d);
                        return a
                    }, t.nextUid = 1, t.matchesSelector = function () {
                        var t = document.createElement("div");
                        return "function" == typeof t.webkitMatchesSelector ? "webkitMatchesSelector" : "function" == typeof t.mozMatchesSelector ? "mozMatchesSelector" : "function" == typeof t.msMatchesSelector ? "msMatchesSelector" : "matchesSelector"
                    }(), t
                }(),
                b = /^([a-zA-Z:_]+[a-zA-Z0-9_\-:\.]*)$/,
                _ = function () {
                    function t(e) {
                        var r = this;
                        this.connected = !1, this.options = t.validateOptions(e), this.observerOptions = t.createObserverOptions(this.options.queries), this.root = this.options.rootNode, this.callback = this.options.callback, this.elementFilter = Array.prototype.concat.apply([], this.options.queries.map(function (t) {
                            return t.elementFilter ? t.elementFilter : []
                        })), this.elementFilter.length || (this.elementFilter = void 0), this.calcReordered = this.options.queries.some(function (t) {
                            return t.all
                        }), this.queryValidators = [], t.createQueryValidator && (this.queryValidators = this.options.queries.map(function (e) {
                            return t.createQueryValidator(r.root, e)
                        })), this.observer = new u(function (t) {
                            r.observerCallback(t)
                        }), this.reconnect()
                    }
                    return t.createObserverOptions = function (t) {
                        function e(t) {
                            if (!n.attributes || r) {
                                if (n.attributes = !0, n.attributeOldValue = !0, !t) return void(r = void 0);
                                r = r || {}, t.forEach(function (t) {
                                    r[t] = !0, r[t.toLowerCase()] = !0
                                })
                            }
                        }
                        var r, n = {
                            childList: !0,
                            subtree: !0
                        };
                        return t.forEach(function (t) {
                            if (t.characterData) return n.characterData = !0, void(n.characterDataOldValue = !0);
                            if (t.all) return e(), n.characterData = !0, void(n.characterDataOldValue = !0);
                            if (t.attribute) return void e([t.attribute.trim()]);
                            var r = s(t.elementFilter).concat(t.attributeList || []);
                            r.length && e(r)
                        }), r && (n.attributeFilter = Object.keys(r)), n
                    }, t.validateOptions = function (e) {
                        for (var r in e)
                            if (!(r in t.optionKeys)) throw Error("Invalid option: " + r);
                        if ("function" != typeof e.callback) throw Error("Invalid options: callback is required and must be a function");
                        if (!e.queries || !e.queries.length) throw Error("Invalid options: queries must contain at least one query request object.");
                        for (var n = {
                            callback: e.callback,
                            rootNode: e.rootNode || document,
                            observeOwnChanges: !!e.observeOwnChanges,
                            oldPreviousSibling: !!e.oldPreviousSibling,
                            queries: []
                        }, s = 0; s < e.queries.length; s++) {
                            var a = e.queries[s];
                            if (a.all) {
                                if (Object.keys(a).length > 1) throw Error("Invalid request option. all has no options.");
                                n.queries.push({
                                    all: !0
                                })
                            } else if ("attribute" in a) {
                                var u = {
                                    attribute: i(a.attribute)
                                };
                                if (u.elementFilter = y.parseSelectors("*[" + u.attribute + "]"), Object.keys(a).length > 1) throw Error("Invalid request option. attribute has no options.");
                                n.queries.push(u)
                            } else if ("element" in a) {
                                var d = 0,
                                    h = Object.keys(a);
                                h.forEach(function (t) {
                                    a.hasOwnProperty(t) && d++
                                });
                                var u = {
                                    element: a.element,
                                    elementFilter: y.parseSelectors(a.element)
                                };
                                if (a.hasOwnProperty("elementAttributes") && (u.attributeList = o(a.elementAttributes), d--), d > 1) throw Error("Invalid request option. element only allows elementAttributes option.");
                                n.queries.push(u)
                            } else {
                                if (!a.characterData) throw Error("Invalid request option. Unknown query request.");
                                if (Object.keys(a).length > 1) throw Error("Invalid request option. characterData has no options.");
                                n.queries.push({
                                    characterData: !0
                                })
                            }
                        }
                        return n
                    }, t.prototype.createSummaries = function (t) {
                        if (!t || !t.length) return [];
                        for (var e = new f(this.root, t, this.elementFilter, this.calcReordered, this.options.oldPreviousSibling), r = [], n = 0; n < this.options.queries.length; n++) r.push(new g(e, this.options.queries[n]));
                        return r
                    }, t.prototype.checkpointQueryValidators = function () {
                        this.queryValidators.forEach(function (t) {
                            t && t.recordPreviousState()
                        })
                    }, t.prototype.runQueryValidators = function (t) {
                        this.queryValidators.forEach(function (e, r) {
                            e && e.validate(t[r])
                        })
                    }, t.prototype.changesToReport = function (t) {
                        return t.some(function (t) {
                            var e = ["added", "removed", "reordered", "reparented", "valueChanged", "characterDataChanged"];
                            if (e.some(function (e) {
                                return t[e] && t[e].length
                            })) return !0;
                            if (t.attributeChanged) {
                                var r = Object.keys(t.attributeChanged),
                                    n = r.some(function (e) {
                                        return !!t.attributeChanged[e].length
                                    });
                                if (n) return !0
                            }
                            return !1
                        })
                    }, t.prototype.observerCallback = function (t) {
                        this.options.observeOwnChanges || this.observer.disconnect();
                        var e = this.createSummaries(t);
                        this.runQueryValidators(e), this.options.observeOwnChanges && this.checkpointQueryValidators(), this.changesToReport(e) && this.callback(e), !this.options.observeOwnChanges && this.connected && (this.checkpointQueryValidators(), this.observer.observe(this.root, this.observerOptions))
                    }, t.prototype.reconnect = function () {
                        if (this.connected) throw Error("Already connected");
                        this.observer.observe(this.root, this.observerOptions), this.connected = !0, this.checkpointQueryValidators()
                    }, t.prototype.takeSummaries = function () {
                        if (!this.connected) throw Error("Not connected");
                        var t = this.createSummaries(this.observer.takeRecords());
                        return this.changesToReport(t) ? t : void 0
                    }, t.prototype.disconnect = function () {
                        var t = this.takeSummaries();
                        return this.observer.disconnect(), this.connected = !1, t
                    }, t.NodeMap = h, t.parseElementFilter = y.parseSelectors, t.optionKeys = {
                        callback: !0,
                        queries: !0,
                        rootNode: !0,
                        oldPreviousSibling: !0,
                        observeOwnChanges: !0
                    }, t
                }();
            e.exports = _
        }, {}
    ],
    20: [
        function (t, e, r) {
            Date.now || (Date.now = function () {
                return +new Date
            }), String.prototype.trim || (String.prototype.trim = function () {
                var t, e, r;
                return e = /^\s+/, r = /\s+$/, t = function () {
                    return this.replace(e, "").replace(r, "")
                }
            }()), Array.isArray || (Array.isArray = function (t) {
                return "[object Array]" === Object.prototype.toString.call(t)
            }), "function" != typeof Object.assign && (Object.assign = function (t) {
                "use strict";
                if (null == t) throw new TypeError("Cannot convert undefined or null to object");
                t = Object(t);
                for (var e = 1; e < arguments.length; e++) {
                    var r = arguments[e];
                    if (null != r)
                        for (var n in r) Object.prototype.hasOwnProperty.call(r, n) && (t[n] = r[n])
                }
                return t
            })
        }, {}
    ],
    21: [
        function (t, e, r) {
            MutationSummary = t("./mutation-summary");
            var n = {
                    SCRIPT: 1,
                    STYLE: 1,
                    NOSCRIPT: 1,
                    IFRAME: 1,
                    BR: 1,
                    FONT: 1,
                    tspan: 1,
                    text: 1,
                    g: 1,
                    rect: 1,
                    path: 1,
                    defs: 1,
                    clipPath: 1,
                    desc: 1,
                    title: 1,
                    use: 1
                },
                i = ["TR", "LI", "DL"],
                o = /^(clear|clearfix|active|hover|enabled|hidden|display|focus|disabled|ng-|growing-)/,
                s = ["button", "submit"],
                a = ["I", "SPAN", "EM", "svg"],
                u = ["A", "BUTTON"],
                d = function () {
                    function t(t, e, r) {
                        var n = this;
                        this.target = t, this.mirror = e;
                        for (var i = [], o = t.firstChild; o; o = o.nextSibling) {
                            var s = this.serializeNode(o);
                            null !== s && i.push(s)
                        }
                        setTimeout(function () {
                            n.mirror.initialize(i)
                        }, 0);
                        var a = [{
                            element: "*"
                        }, {
                            element: "*",
                            elementAttributes: "data-growing-title src"
                        }];
                        r && (a = a.concat(r)), this.mutationSummary = new MutationSummary({
                            rootNode: t,
                            callback: function (t) {
                                n.applyChanged(t)
                            }, queries: a
                        })
                    }
                    return t.prototype.disconnect = function () {
                        this.mutationSummary && (this.mutationSummary.disconnect(), this.mutationSummary = void 0)
                    }, t.prototype.serializeNode = function (t, e, r, d) {
                        if (null === t) return null;
                        if (1 === n[t.tagName]) return null;
                        if (void 0 === e) {
                            e = "/";
                            for (var h = t.parentElement; h && "BODY" !== h.tagName && "HTML" !== h.tagName;) {
                                var c = "/" + h.tagName.toLowerCase(),
                                    l = h.getAttribute("id");
                                if (l && null === l.match(/^[0-9]/) && (c += "#" + l), h.hasAttribute("class"))
                                    for (var p = h.getAttribute("class").trim().split(/\s+/).sort(), f = 0; f < p.length; f++) p[f].length > 0 && null === o.exec(p[f]) && (c += "." + p[f]);
                                e = c + e, h = h.parentElement
                            }
                        }
                        var g = {
                            nodeType: t.nodeType
                        };
                        switch (1 === g.nodeType && -1 !== u.indexOf(t.tagName) && (g.dom = t), g.nodeType) {
                        case 10:
                            var v = t;
                            g.name = v.name, g.publicId = v.publicId, g.systemId = v.systemId;
                            break;
                        case 8:
                            return null;
                        case 3:
                            if ("/" === e || 0 === t.textContent.trim().length) return null;
                            g.textContent = t.textContent.replace(/[\n \t]+/g, " ").trim(), g.textContent.length > 0 && (g.leaf = !0, g.text = g.textContent, g.path = e.slice(0, -1));
                            break;
                        case 1:
                            if (!t.style) return null;
                            var m = t.style.display;
                            if ("block" !== m && "inline" !== m && ("none" === m || "none" === window.getComputedStyle(t).display) && "A" !== t.tagName && null === t.querySelector("a")) return null;
                            var w = t;
                            if (g.tagName = w.tagName, g.attributes = {
                                any: w.hasAttributes()
                            }, this.appendDataAttrs(g, w), e += w.tagName.toLowerCase(), w.hasAttribute("id") && null === w.getAttribute("id").match(/^[0-9]/) && (e += "#" + w.getAttribute("id")), "INPUT" == w.tagName && w.hasAttribute("name")) e += "." + w.getAttribute("name");
                            else if (w.hasAttribute("class")) {
                                p = w.getAttribute("class").trim().split(/\s+/).sort();
                                for (var f = 0; f < p.length; f++) p[f].length > 0 && null === o.exec(p[f]) && (e += "." + p[f])
                            }
                            w.hasAttribute("href") && (g.attributes.href = w.getAttribute("href"));
                            var y, b = !0;
                            if (e += "/", w.childNodes.length > 0) {
                                if (g.childNodes = [], w.hasAttribute("growing-ignore") || w.hasAttribute("data-growing-ignore")) return null;
                                var _, C, N = 0,
                                    S = -1 !== u.indexOf(w.tagName);
                                if (S)
                                    for (var E = w.firstChild; E; E = E.nextSibling)
                                        if (1 === E.nodeType && -1 === a.indexOf(E.tagName)) {
                                            S = !1;
                                            break
                                        }
                                for (var x = w.firstChild; x; x = x.nextSibling) {
                                    if (_ = w.hasAttribute("data-growing-info") ? w.getAttribute("data-growing-info") : null, C = w.hasAttribute("data-growing-idx") ? parseInt(w.getAttribute("data-growing-idx")) : -1, 1 === x.nodeType) {
                                        if (1 === n[x.tagName]) {
                                            b = !1;
                                            continue
                                        }
                                        if (x.hasAttribute("growing-ignore") || x.hasAttribute("data-growing-ignore")) continue;
                                        if (S && -1 !== a.indexOf(x.tagName)) {
                                            b = !1;
                                            continue
                                        } - 1 !== i.indexOf(x.tagName) && (N += 1, C = N), x.hasAttribute("data-growing-idx") && (N = parseInt(x.getAttribute("data-growing-idx")), C = N), x.hasAttribute("data-growing-info") && (_ = x.getAttribute("data-growing-info"))
                                    }
                                    var I = this.serializeNode(x, e, N > 0 && C > 0 ? N : r, _ || d);
                                    if (null === I) 3 != x.nodeType && (b = !1);
                                    else if ("undefined" != typeof I.childNodes) {
                                        b = !1, y = !0;
                                        for (var O = 0; O < I.childNodes.length; O++)
                                            if (I.childNodes[O].tagName) {
                                                y = !1;
                                                break
                                            }
                                        N > 0 && C > 0 ? I.idx = N : r && (I.idx = r), _ ? I.obj = _ : d && (I.obj = d), g.childNodes.push(I)
                                    } else {
                                        if ((0 === w.offsetWidth || 0 === w.offsetHeight) && "A" !== w.tagName && "BUTTON" !== w.tagName) return null;
                                        I.leaf && (r && (I.idx = r), d && (I.obj = d), g.childNodes.push(I))
                                    }
                                }
                            } else g.childNodes = []; if (b)
                                if (g.leaf = !0, "IMG" === w.tagName && (delete g.attributes.href, w.src && -1 === w.src.indexOf("data:image") && (g.attributes.href = w.src)), w.hasAttribute("data-growing-title") && w.getAttribute("data-growing-title").length > 0) g.text = w.getAttribute("data-growing-title");
                                else if (w.hasAttribute("title") && w.getAttribute("title").length > 0) g.text = w.getAttribute("title");
                            else if ("IMG" === w.tagName) {
                                if (w.alt) g.text = w.alt;
                                else if (g.attributes.href) {
                                    var A = g.attributes.href.split("?")[0];
                                    if (A) {
                                        var T = A.split("/");
                                        T.length > 0 && (g.text = T[T.length - 1])
                                    }
                                }
                            } else if ("INPUT" === w.tagName && -1 !== s.indexOf(w.type)) g.text = w.value;
                            else if ("svg" === w.tagName) {
                                for (var P = w.firstChild; P; P = P.nextSibling)
                                    if ("use" === P.tagName && P.getAttribute("xlink:href")) {
                                        g.text = P.getAttribute("xlink:href");
                                        break
                                    }
                            } else {
                                var k = w.textContent.trim();
                                if (0 === k.length && "I" !== w.tagName && "A" !== w.tagName) return null;
                                g.text = k
                            } else w.hasAttribute("data-growing-title") && w.getAttribute("data-growing-title").length > 0 ? g.text = w.getAttribute("data-growing-title") : w.hasAttribute("title") && w.getAttribute("title").length > 0 && (g.text = w.getAttribute("title")), w.hasAttribute("data-growing-idx") && (g.idx = parseInt(w.getAttribute("data-growing-idx"))), w.hasAttribute("data-growing-info") && (g.obj = w.getAttribute("data-growing-info"));
                            g.path = e.slice(0, -1)
                        }
                        return g
                    }, t.prototype.appendDataAttrs = function (t, e) {}, t.prototype.serializeAddedAndMoved = function (t, e, r) {
                        var o = this,
                            s = t.concat(e).concat(r);
                        if (0 === s.length) return [];
                        var a = new MutationSummary.NodeMap,
                            u = {};
                        s.forEach(function (t) {
                            t && (u[a.nodeId(t)] = !0)
                        });
                        var d = [];
                        s.forEach(function (t) {
                            if (t && 1 !== n[t.tagName]) {
                                var e = t.parentNode;
                                if (e && !u[a.nodeId(e)] && "undefined" != typeof e.getAttribute) {
                                    var r = e.getAttribute("id"),
                                        i = e.getAttribute("class"),
                                        o = t.getAttribute("class");
                                    if (!r || -1 === r.toLowerCase().indexOf("clock") && -1 === r.toLowerCase().indexOf("countdown") && -1 === r.toLowerCase().indexOf("time"))
                                        if (!i || -1 === i.toLowerCase().indexOf("clock") && -1 === i.toLowerCase().indexOf("countdown") && -1 === i.toLowerCase().indexOf("time"))
                                            if (e.getAttribute("data-countdown"));
                                            else if (o && -1 !== o.indexOf("daterangepicker"));
                                    else if (t.hasAttribute("growing-ignore") || t.hasAttribute("data-growing-ignore"));
                                    else {
                                        for (; e && e !== document && "#document-fragment" !== e.nodeName && "BODY" !== e.tagName && !e.hasAttribute("growing-ignore") && !e.hasAttribute("data-growing-ignore");) e = e.parentNode;
                                        (null === e || "BODY" === e.tagName || "#document-fragment" === e.nodeName) && d.push(t)
                                    } else;
                                    else;
                                }
                            }
                        });
                        var h = [];
                        return d.forEach(function (t) {
                            for (var e = void 0, r = t; r && "BODY" !== r.tagName && -1 === i.indexOf(r.tagName);) r = r.parentNode;
                            if (r && "BODY" !== r.tagName) {
                                var n = r.parentNode;
                                if (null == n) return;
                                for (var s = 1, a = n.childNodes[s - 1]; s <= n.childNodes.length; s++)
                                    if (a.tagName === r.tagName && a === r) {
                                        e = s;
                                        break
                                    }
                            }
                            var u = o.serializeNode(t, void 0, e);
                            null !== u && h.push(u)
                        }), delete u, delete d, h
                    }, t.prototype.serializeValueChanges = function (t) {
                        var e = this,
                            r = [],
                            n = new MutationSummary.NodeMap;
                        return t.forEach(function (t) {
                            var r = n.get(t);
                            r || (r = e.serializeNode(t), n.set(t, r))
                        }), n.keys().forEach(function (t) {
                            var e = n.get(t);
                            e && r.push(e)
                        }), r
                    }, t.prototype.applyChanged = function (t) {
                        var e = this,
                            r = t[0],
                            n = r.added,
                            i = t[1];
                        setTimeout(function () {
                            var t = e.serializeAddedAndMoved(n, [], []),
                                r = [].concat(i.attributeChanged["data-growing-title"], i.attributeChanged.src);
                            if (r && r.length > 0) {
                                var o = e.serializeValueChanges(r);
                                if (o && o.length > 0)
                                    for (var s = 0; s < o.length; s++) {
                                        var a = o[s];
                                        a.text && a.text.length > 0 && (t = t.concat(a))
                                    }
                            }
                            e.mirror.applyChanged([], t)
                        }, 10)
                    }, t
                }();
            r.Client = d
        }, {
            "./mutation-summary": 19
        }
    ],
    22: [
        function (t, e, r) {
            var n, i, o, s, a, u, d, h;
            i = window._vds_ios, i && (h = null != (a = window.webkit) && null != (u = a.messageHandlers) ? u.GrowingIO_WKWebView : void 0, d = !h), o = window._vds_bridge ? !0 : void 0, s = i ? !0 : void 0, n = window._vds_bridge || i, e.exports = {
                isAndroid: o,
                isiOS: i,
                enabled: n,
                withUIWebView: d,
                withWKWebView: h,
                calcScale: function () {
                    var t, e, r, n, i, s, a, u;
                    if (r = 1, i = document.querySelector('meta[name="viewport"]'), i && i.content)
                        for (a = i.content.split(","), e = 0, n = a.length; n > e; e++)
                            if (s = a[e], t = s.split("="), 2 === t.length && "initial-scale" === t[0].trim() && (u = parseFloat(t[1]), u !== 0 / 0)) {
                                r = u;
                                break
                            }
                    return o && (r = window.screen.width * window.devicePixelRatio / window.innerWidth, r > window.devicePixelRatio && (r = window.devicePixelRatio)), 1 === r && window._vds_hybrid_config.phoneWidth && window.innerWidth !== window._vds_hybrid_config.phoneWidth && (r = window._vds_hybrid_config.phoneWidth / window.innerWidth), r
                }
            }
        }, {}
    ],
    23: [
        function (t, e, r) {
            var n, i, o, s, a = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) u.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                u = {}.hasOwnProperty;
            n = t("../../core/info/dom_info"), i = t("./gr_user_info"), o = function (t) {
                function e() {
                    return e.__super__.constructor.apply(this, arguments)
                }
                return a(e, t), e.prototype.user = function () {
                    return new i
                }, e
            }(n), s = new o, e.exports = s
        }, {
            "../../core/info/dom_info": 8,
            "./gr_user_info": 24
        }
    ],
    24: [
        function (t, e, r) {
            var n, i, o = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) s.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                s = {}.hasOwnProperty;
            n = t("../../core/info/gr_user_info"), i = function (t) {
                function e() {
                    return e.__super__.constructor.apply(this, arguments)
                }
                return o(e, t), e.prototype.isHybrid = !0, e.prototype.cookieDomain = function () {
                    return window._vds_hybrid_native_info ? window._vds_hybrid_native_info.d : ""
                }, e.prototype.vid = function () {
                    return window._vds_hybrid_native_info ? window._vds_hybrid_native_info.u : ""
                }, e.prototype.hasSid = function () {
                    return window._vds_hybrid_native_info && window._vds_hybrid_native_info.s ? !0 : !1
                }, e.prototype.sid = function () {
                    return window._vds_hybrid_native_info ? window._vds_hybrid_native_info.s : ""
                }, e.prototype.getCs1 = function () {
                    return window._vds_hybrid_native_info ? window._vds_hybrid_native_info.cs1 : ""
                }, e.prototype.updateCS1 = function (t, e) {
                    null == e && (e = this.sessionId)
                }, e.prototype.clearCs1 = function () {}, e.prototype.updateSessionId = function (t, e, r) {}, e.prototype.isSendNewVisit = function () {
                    return !0
                }, e
            }(n), e.exports = i
        }, {
            "../../core/info/gr_user_info": 9
        }
    ],
    25: [
        function (t, e, r) {
            var n, i, o, s, a, u, d, h = function (t, e) {
                    return function () {
                        return t.apply(e, arguments)
                    }
                },
                c = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) l.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                l = {}.hasOwnProperty;
            i = t("./observer/dom_observer"), s = t("./sender"), u = t("./hybrid_mode_detector"), n = t("../core/base_launcher"), d = t("./info/dom_info"), a = t("./vendor/tree-mirror"), o = function (t) {
                function e() {
                    this.defaultSamplingFunc = h(this.defaultSamplingFunc, this), this.user = d.user(), e.__super__.constructor.call(this), this.sender = new s(this.user, this.callback), this.observer = new i(this.sender, this.user, this, a)
                }
                return c(e, t), e.prototype.afterInitialize = function () {
                    return window.vds.hybridVersion = this.version, window._vds_hybrid ? void 0 : window._vds_hybrid = {
                        scale: u.calcScale(),
                        track: function (t) {
                            return function () {
                                var e;
                                return (e = t.system).track.apply(e, arguments)
                            }
                        }(this),
                        isMoving: !1,
                        startTracing: function (t) {
                            return u.enabled = !0, u.isiOS = !0, u.withUIWebView = "UIWebView" === t, u.withWKWebView = !u.withUIWebView
                        }, saveEvent: function (t) {
                            return function () {
                                var e;
                                return (e = t.sender).send.apply(e, arguments)
                            }
                        }(this),
                        pollEvents: function (t) {
                            return function () {
                                var e;
                                return (e = t.sender).pollEvents.apply(e, arguments)
                            }
                        }(this),
                        setNativeInfo: function (t) {
                            return _vds_hybrid.nativeInfo = t
                        }, impressAllElements: function (t) {
                            return function () {
                                return t.observer.trackPageView(), null != t.observer.client ? t.observer.client.impressElement(document.body) : void 0
                            }
                        }(this),
                        snapshotAllElements: function (t) {
                            return function () {
                                var e;
                                try {
                                    return t.observer.client.snapshoting = !0, null != t.observer.client && t.observer.client.impressElement(document.body), t.observer.client.snapshoting = !1
                                } catch (r) {
                                    e = r
                                }
                            }
                        }(this)
                    }
                }, e.prototype.hybridSetUserId = function (t) {
                    var e;
                    if (t && !(t.length > 1e3)) return window._vds_hybrid_native_info && (window._vds_hybrid_native_info.cs1 = t), window._vds_bridge && window._vds_bridge.setUserId ? window._vds_bridge.setUserId(t) : window._vds_ios ? (e = {
                        t: "hybridSetUserID",
                        userID: t
                    }, this.sender.nativeQueueReady = !0, this.sender.sendQueue(e)) : void 0
                }, e.prototype.hybridClearUserId = function () {
                    var t;
                    return window._vds_hybrid_native_info && delete window._vds_hybrid_native_info.cs1, window._vds_bridge && window._vds_bridge.clearUserId ? window._vds_bridge.clearUserId("null") : window._vds_ios ? (t = {
                        t: "hybridClearUserID"
                    }, this.sender.nativeQueueReady = !0, this.sender.sendQueue(t)) : void 0
                }, e.prototype.hybridSetVisitor = function (t) {
                    var e;
                    if (t) return window._vds_bridge && window._vds_bridge.setVisitor ? window._vds_bridge.setVisitor(JSON.stringify(t)) : window._vds_ios ? (e = {
                        t: "hybridSetVisitor",
                        visitorJson: t
                    }, this.sender.nativeQueueReady = !0, this.sender.sendQueue(e)) : void 0
                }, e.prototype.defaultSamplingFunc = function () {}, e.prototype.beforeConnect = function () {
                    return !0
                }, e
            }(n), e.exports = o
        }, {
            "../core/base_launcher": 1,
            "./hybrid_mode_detector": 22,
            "./info/dom_info": 23,
            "./observer/dom_observer": 27,
            "./sender": 28,
            "./vendor/tree-mirror": 29
        }
    ],
    26: [
        function (t, e, r) {
            var n, i, o, s, a, u, d, h, c, l, p, f, g, v = [].slice;
            if (t("../core/vendor/shim"), n = t("./launcher"), window.launcher) return void console.log("already inserted script");
            for (window.launcher = a = new n, h = [], window.gio && window.gio.hybrid ? h = window.gio.hybrid : ((null != (c = window.gio) ? c.q : void 0) && (h = h.concat(window.gio.q)), window._vds && (h = h.concat(window._vds))), s = 0, u = h.length; u > s; s++) d = h[s], null != a && a.apply.apply(a, d);
            o = function () {
                var t;
                return t = 1 <= arguments.length ? v.call(arguments, 0) : [], a.apply.apply(a, t)
            }, o.platform = "hybrid", o.push = function () {
                return arguments.length > 1 ? a.apply.apply(a, arguments) : a.apply.apply(a, arguments[0])
            }, window.gioGlobalArray = window.gioGlobalArray || [], window.gioGlobalArray.push(o), window.gio = function () {
                var t, e, r, n, i;
                for (n = window.gioGlobalArray, e = 0, r = n.length; r > e; e++) t = n[e], "init" === (i = arguments[0]) || "send" === i ? ("init" === arguments[0] && 1 === window.gioGlobalArray.length && (window.gio.web = h), "web" === t.platform && t.apply(null, arguments)) : t.apply(null, arguments);
                return window.gio && window.gio.web ? window.gio.web.push(arguments) : void 0
            }, window.gio.hybrid = void 0, (null != (l = window._vds_hybrid_config) ? l.enableHT : void 0) && o.push(["enableHT", !0]), (null != (p = window._vds_hybrid_config) ? p.disableImp : void 0) && o.push(["setImp", !1]), i = "fakeAccountID", (null != (f = window.vds) ? f.accountId : void 0) ? i = window.vds.accountId : (null != (g = window._vds_hybrid_native_info) ? g.ai : void 0) && (i = window._vds_hybrid_native_info.ai), o("init", i, {
                setTrackerScheme: "http"
            }), o("send")
        }, {
            "../core/vendor/shim": 20,
            "./launcher": 25
        }
    ],
    27: [
        function (t, e, r) {
            var n, i, o, s, a = function (t, e) {
                    function r() {
                        this.constructor = t
                    }
                    for (var n in e) u.call(e, n) && (t[n] = e[n]);
                    return r.prototype = e.prototype, t.prototype = new r, t.__super__ = e.prototype, t
                },
                u = {}.hasOwnProperty;
            n = t("../../core/observer/dom_observer"), s = t("../info/dom_info"), o = t("../hybrid_mode_detector"), i = function (t) {
                function e(t, r, n, i) {
                    this.sender = t, this.gruser = r, this.launcher = n, this.TreeMirror = i, this.info = s, e.__super__.constructor.call(this, this.sender, this.gruser, this.launcher, this.TreeMirror)
                }
                return a(e, t), e.prototype.snapshotCallback = function (t, e) {
                    return e.seqid = t.seqid, e.sk = t.sk, e.et = t.et, t.snapshotCallback(e)
                }, e.prototype.appendMessageAttrs = function (t, e, r) {
                    return t.snapshoting && (e.ex = r.ex, e.ey = r.ey, e.ew = r.ew, e.eh = r.eh), e
                }, e.prototype.appendElemAttrs = function (t, e, r, n) {
                    var i, s, a, u, d, h, c;
                    if (o.enabled && e && r.getBoundingClientRect && (c = r.getBoundingClientRect(), t.ex = c.left, t.ey = c.top, t.ew = c.width, t.eh = c.height, n.containerMessage.length > 0 && n.pnode)) {
                        for (s = n.pnode.getBoundingClientRect(), d = n.containerMessage, h = [], a = 0, u = d.length; u > a; a++) i = d[a], i.ex = s.left, i.ey = s.top, i.ew = s.width, h.push(i.eh = s.height);
                        return h
                    }
                }, e.prototype.initServerImpSetting = function () {
                    return !0
                }, e.prototype.blind = function () {
                    return !1
                }, e.prototype.trackPageView = function () {
                    return this.pageService.sendPV({
                        useNewTime: !0
                    }, this.sendPageCallback)
                }, e.prototype.resendPage = function (t) {
                    return this.pageService.sendPV({
                        useNewTime: t
                    }, this.sendPageCallback)
                }, e
            }(n), e.exports = i
        }, {
            "../../core/observer/dom_observer": 11,
            "../hybrid_mode_detector": 22,
            "../info/dom_info": 23
        }
    ],
    28: [
        function (t, e, r) {
            var n, i, o, s = function (t, e) {
                return function () {
                    return t.apply(e, arguments)
                }
            };
            i = t("./hybrid_mode_detector"), o = t("../core/tools/utils"), n = function () {
                function t(t, e) {
                    this.user = t, this.callback = e, this.sendWithIframe = s(this.sendWithIframe, this),
                        this.sendQueue = s(this.sendQueue, this), this._send = s(this._send, this), this.send = s(this.send, this), this.eventCounter = 0, this.nativeQueueReady = !0, this.dataIframe = null, this.messageQueue = []
                }
                return t.prototype.connect = function () {
                    return "function" == typeof this.callback ? this.callback(this.user, this.send) : void 0
                }, t.prototype.pollEvents = function () {
                    var t;
                    return (null != (t = this.messageQueue) ? t.length : void 0) ? (this.sendWithIframe(this.messageQueue), this.messageQueue = []) : this.nativeQueueReady = !0
                }, t.prototype.send = function (t, e) {
                    var r, n, i, s;
                    if (o.isArray(t)) {
                        for (s = [], r = 0, n = t.length; n > r; r++) i = t[r], s.push(this._send(i, e));
                        return s
                    }
                    return this._send(i, e)
                }, t.prototype._send = function (t, e) {
                    var r, n, s, a;
                    if (t = t, t.d = o.domain_for_send(), window._vds_hybrid_native_info && window._vds_hybrid_native_info.cs1 ? t.cs1 = window._vds_hybrid_native_info.cs1 : delete t.cs1, "snap" === t.t)
                        for (t.e = t.e.reverse(), a = t.e, n = 0, s = a.length; s > n; n++) r = a[n], r.nodeType = "hybrid", r.ex *= _vds_hybrid.scale, r.ey *= _vds_hybrid.scale, r.ew *= _vds_hybrid.scale, r.eh *= _vds_hybrid.scale;
                    if (t.tl && (t.v = t.tl), i.isAndroid) {
                        if ("snap" === t.t) return window._vds_bridge.hoverNodes(JSON.stringify(t));
                        if ("vst" !== t.t) return "cstm" === t.t && window._vds_bridge.saveCustomEvent ? window._vds_bridge.saveCustomEvent(JSON.stringify(t)) : window._vds_bridge.saveEvent(JSON.stringify(t))
                    } else if (i.isiOS) return this.sendQueue(t)
                }, t.prototype.sendQueue = function (t) {
                    return i.withUIWebView ? this.nativeQueueReady ? this.sendWithIframe([t]) : this.messageQueue.push(t) : i.withWKWebView ? window.webkit.messageHandlers.GrowingIO_WKWebView.postMessage([t]) : void 0
                }, t.prototype.sendWithIframe = function (t) {
                    var e;
                    return this.nativeQueueReady = !1, e = "https://api.growingio.com/growinghybridsdk-" + this.eventCounter+++"?" + encodeURIComponent(JSON.stringify(t)), this.dataIframe ? this.dataIframe.src = e : (this.dataIframe = document.createElement("iframe"), this.dataIframe.style.display = "none", this.dataIframe.style.position = "fixed", this.dataIframe.style.width = 0, this.dataIframe.style.height = 0, this.dataIframe.style.border = "none", this.dataIframe.src = e, document.body.appendChild(this.dataIframe))
                }, t
            }(), e.exports = n
        }, {
            "../core/tools/utils": 16,
            "./hybrid_mode_detector": 22
        }
    ],
    29: [
        function (t, e, r) {
            TreeMirrorClient = t("../../core/vendor/tree-mirror"), TreeMirrorClient.Client.prototype.impressElement = function (t) {
                for (var e = [], r = t.firstChild; r; r = r.nextSibling) {
                    var n = this.serializeNode(r);
                    null !== n && e.push(n)
                }
                this.mirror.initialize(e)
            }, TreeMirrorClient.Client.prototype.appendDataAttrs = function (t, e) {
                if (this.snapshoting && e.getBoundingClientRect) {
                    var r = e.getBoundingClientRect();
                    t.ex = r.left, t.ey = r.top, t.ew = r.width, t.eh = r.height
                }
            }, e.exports = TreeMirrorClient
        }, {
            "../../core/vendor/tree-mirror": 21
        }
    ]
}, {}, [26]);