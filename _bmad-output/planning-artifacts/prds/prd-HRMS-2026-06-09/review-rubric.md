# PRD Quality Review — HRMS

## Overall verdict

This is a well-structured, substantive PRD that would genuinely guide a development team. The FRs are concrete and testable, the glossary is thorough, and the multi-tenant extension from the brief is justified clearly. Two weaknesses hold it back from strong: (1) FR numbering jumps around (1-26, then 27-40) making downstream traceability fragile, and (2) several assumptions about approval-workflow edge cases and tenant lifecycle carry real implementation risk but sit as quiet inline tags rather than surfaced decisions. A decision-maker can act on this PRD today for the core HR modules; the approval-workflow and multi-tenant configuration areas need one more pass to close gaps.

## Decision-readiness — adequate

The PRD opens with a clear statement of what changed from the brief (Section 0: SaaS-ready multi-tenant, audit log, documents, dashboard added) and names the trade-off: "Brief ban dau scope self-hosted don tenant; PRD mo rong nen tang de khong phai retrofit sau." This is honest and useful. The Vision (Section 1) names concrete competitors and prices, which grounds the differentiation claim in reality rather than aspiration.

However, several real tensions are buried as assumptions rather than surfaced as decisions requiring sign-off:
- The single-userRole-per-user assumption (Section 4.1) is a genuine architectural constraint for Vietnamese SMEs where HR staff often double as admin and manager. It is stated as an `[ASSUMPTION]` but should be a called-out decision with the trade-off named.
- The "no carry-over of leave days" assumption (FR-15) is flagged as Open Question #1 but also asserted as an assumption in the same FR. This mixed signal means a builder will just pick one.
- Escalation behavior when an approver is unavailable (FR-40) -- "escalate to Admin" -- is a significant workflow choice that could surprise users, presented as an assumption rather than a decision.

### Findings
- **high** Single-userRole constraint needs decision status (Section 4.1) -- This is stated as `[ASSUMPTION]` but is a load-bearing architectural choice that affects RBAC design, JWT structure, and UX. Vietnamese SME staff frequently hold multiple roles. *Fix:* Promote to an explicit decision with trade-off noted (simplicity vs. flexibility), or add a `[NOTE FOR PM]` requesting validation.
- **medium** Leave carry-over is both assumed and questioned (FR-15 + Open Question #1) -- The assumption says "Khong chuyen sang nam sau" but OQ-1 asks for confirmation. The builder cannot tell which to follow. *Fix:* Remove the assumption and keep only the Open Question, or resolve the question and remove it from OQ list.
- **medium** Approver-unavailable escalation path (FR-40) -- Escalating to Admin when an approver is disabled is a workflow design decision, not an inference. Different tenants may want different behavior. *Fix:* Surface as a decision; consider whether this should be configurable per-tenant or fixed.

## Substance over theater — strong

This PRD earns its content. Every persona (System Admin, HR Admin, Manager, Employee) drives multiple FRs and appears as a protagonist in User Journeys. There is no fifth persona added for thoroughness. The four JTBD statements (Section 2.1) are tightly scoped and each connects to real pain ("thay vi danh 2-3 ngay tinh tay tren Excel").

The Vision (Section 1) names specific competitors with pricing -- Base.vn, AMIS HRM, 1Office -- and states the differentiation thesis (open-source, multi-tenant ready, affordable for Vietnamese market). This is earned, not template filler.

NFRs (Section 8) carry specific thresholds: bcrypt cost >= 12, JWT TTL 30min/7d, API < 2s at 500 concurrent users, payroll for 500 employees < 30s, Excel export < 10s for 500 rows. These are product-specific bounds, not boilerplate.

No findings needed -- this dimension is clean.

## Strategic coherence — strong

The PRD has a clear thesis: replace manual HR processes (Excel/paper) for Vietnamese SMEs with a SaaS-ready platform, betting that multi-tenant architecture from day one avoids costly retrofit later. Every feature serves this arc:
- Core HR (employee, contract, attendance, leave, payroll) solves the daily operational pain.
- Multi-tenant + audit log + approval workflow builds the SaaS platform foundation.
- Dashboard and reports provide immediate value that justifies adoption.

Feature prioritization follows from the thesis. The MVP scope (Section 6) includes everything needed for daily HR operations plus the SaaS infrastructure; V2 items (KPI, recruitment, mobile app, parallel approval) are clearly deferred capabilities that don't block the core value proposition.

Success Metrics validate the thesis directly: SM-1 (payroll in <= 4 hours vs. 2-3 days), SM-2 (100% accuracy on insurance/tax), SM-4 (100% tenant isolation). Counter-metrics SM-C1 (onboarding <= 30 min) and SM-C2 (no increase in payroll errors) guard against optimizing speed at the expense of usability and accuracy.

### Findings
- **low** SM-6 threshold may be under-specified (Section 7) -- "< 2 giay cho cac thao tac thuong dung voi 500 users dong thoi" does not distinguish read vs. write operations or specify which endpoints are "thuong dung." Payroll calculation for 500 employees has its own 30s budget (Section 8), which is appropriate, but the 2s blanket could create confusion during performance testing. *Fix:* Clarify which operations the 2s SLA covers (e.g., list views, form submissions) vs. which have separate budgets.

## Done-ness clarity — strong

This is the PRD's strongest dimension. Every FR has a "Testable" section with specific, verifiable conditions. Examples of well-written testable criteria:
- FR-1: "Dang nhap sai 5 lan lien tiep -> khoa tai khoan 15 phut" -- specific threshold, specific consequence.
- FR-7: "Tao hop dong xac dinh thoi han > 36 thang -> tu choi" -- compliance rule with testable boundary.
- FR-11: "Check-in 8:16 (gio chuan 8:00) -> danh dau 'Di muon'" and "Check-in 8:14 -> binh thuong" -- boundary testing baked in.
- FR-20: "Thu nhap chiu thue 10 trieu -> thue = 5M x 5% + 5M x 10% = 750,000" -- worked example as acceptance criterion.

The payroll formula (FR-19) is explicit: "luong co ban x (ngay cong thuc te / ngay cong chuan) + phu cap + OT - BHXH - BHYT - BHTN - thue TNCN = luong net."

### Findings
- **medium** FR-22 "real-time" is unbounded (Section 4.8) -- "Thong bao real-time trong ung dung" does not specify latency. Is this WebSocket push, polling, or SSE? The implementation difference is significant. *Fix:* Specify notification delivery mechanism or at minimum acceptable latency (e.g., "within 5 seconds of triggering event").
- **low** FR-33 file type validation is a whitelist but virus/malware scanning is absent (Section 4.10) -- The security NFR mentions "validate file type (whitelist), scan size limit" but no mention of malware scanning. For an internal-stakes product this is acceptable for V1, but worth a `[NOTE FOR PM]` for V2. *Fix:* Add a note acknowledging this gap.

## Scope honesty — strong

The Non-Goals section (Section 5) does real work -- 11 explicit exclusions, each with a version target. Notable honest calls:
- "Khong parallel approval" -- acknowledges a real limitation of the workflow engine.
- "Khong cau truc to chuc phan cap sau" -- Department + Position only, no Khoi/Team hierarchy.
- "Khong SSO/LDAP" -- username/password only.

The MVP scope (Section 6) cleanly separates in-scope from out-of-scope with version targets (V2, V2+, V3+). Assumptions are inline-tagged and indexed at the end (Section 11). Open Questions (Section 10) are genuinely open -- none have answers smuggled in.

The PRD is transparent about the brief-to-PRD scope expansion (Section 0), naming multi-tenant, audit log, documents, and dashboard as additions.

### Findings
- **medium** OT approval workflow is ambiguous (Section 4.12) -- The feature description says "nghi phep, OT — mo rong duoc cho module moi" but FR-38/39/40 only specify leave requests. Whether OT requests go through the approval workflow in V1 is unclear. *Fix:* Explicitly state whether OT uses the approval workflow in V1 or is admin-only entry (as FR-18 implies admin input only).
- **low** Open Question density is appropriate (Section 10) -- 7 OQs for an internal-stakes PRD is healthy. None are blockers for architecture or story creation. No fix needed.

## Downstream usability — adequate

The Glossary (Section 3) is thorough -- 18 domain terms with Vietnamese and English names, relationship to other terms, and tenant scoping noted. Terms are used consistently across FRs, UJs, and SMs.

UJs have named protagonists: Lan (HR Admin), Minh (Employee), Hoa (Employee), Tuan (Team Lead), Hung (Manager). Each UJ is grounded in a specific tenant context (abc-corp, XYZ).

The Assumptions Index (Section 11) provides a roundtrip from inline tags to the index. Cross-references between FRs and UJs use "Realizes UJ-X" notation.

However, the FR numbering is non-contiguous and grouped in a confusing order: FR-1 through FR-26 (Sections 4.1-4.8, 4.13), then FR-27 through FR-40 (Sections 4.2, 4.9-4.12). This happened because multi-tenant, audit log, documents, dashboard, and approval workflow were added after the initial FR sequence. A downstream story-creation agent parsing FR IDs sequentially will hit jumps.

### Findings
- **high** FR numbering is non-contiguous and out of order (Sections 4.1-4.13) -- FR-1 to FR-26 cover auth, employee, contract, attendance, leave, payroll, notifications, reports. Then FR-27 to FR-40 cover multi-tenant, audit log, documents, dashboard, approval workflow. Section 4.2 (Multi-tenant) contains FR-27-29 but appears after Section 4.1 (Auth, FR-1-3). Section 4.13 (Reports, FR-24-26) appears after Section 4.12 (Approval, FR-38-40). *Fix:* Renumber FRs to match section order, or add a mapping table. This is a mechanical fix but high-severity because story creation tools will reference FR IDs.
- **medium** Assumptions Index entry mismatch (Section 11) -- The index entry "Section 4.12 — Manager khong xem bao cao trong V1" references Section 4.12 but the actual assumption is in Section 4.13 (Bao cao). The Reports section is 4.13, not 4.12. *Fix:* Correct the section reference to 4.13.
- **low** "Realizes" cross-references are incomplete -- FR-9 (Check-in/out) says "Realizes UJ-2" is in the section header, not the FR. FR-35/36/37 (Dashboard) don't cross-reference UJ-7 at FR level, only at section level. Consistency would help downstream extraction. *Fix:* Add "Realizes UJ-X" to each FR that maps to a UJ.

## Shape fit — strong

This is a multi-stakeholder B2B SaaS product with 4 distinct roles and meaningful UX -- the PRD correctly uses UJs with named protagonists as load-bearing elements. The 8 UJs cover the key workflows: onboarding (UJ-1), daily attendance (UJ-2), leave request with multi-level approval (UJ-3), payroll run (UJ-4), payslip viewing (UJ-5), audit investigation (UJ-6), dashboard overview (UJ-7), and workflow configuration (UJ-8).

The UJ density (8 UJs for 13 features, 40 FRs) is appropriate -- not every feature needs a UJ, and the ones present cover the critical paths. The PRD balances capability specification (multi-tenant, audit log) with user-facing journey specification (leave approval, payroll) correctly.

The formalization level matches internal stakes: detailed enough for a team to build from, not so formal that it becomes ceremony. Vietnamese language output is consistent throughout.

No findings needed.

## Mechanical notes

- **Glossary drift:** Minor -- "Admin" is used in some FRs to mean "HR Admin" (tenant-level), while Section 2.1 defines the JTBD userRole as "HR Admin" and Section 4.1 defines the system userRole as "Admin (HR Admin per-tenant)." The Glossary does not have an entry for "Admin" -- only "System Admin (Super Admin)." This could cause confusion between System Admin and tenant Admin. *Fix:* Add "Admin (HR Admin)" to the Glossary, or use "HR Admin" consistently in FRs.
- **ID continuity:** FR IDs have gaps in section order (see finding above). UJ IDs are contiguous (UJ-1 through UJ-8) but UJ-8 appears before UJ-4 in the document. SM IDs are contiguous and ordered (SM-1 through SM-7, SM-C1, SM-C2).
- **Assumptions Index roundtrip:** 18 inline assumptions, 18 index entries. One section reference error (4.12 vs 4.13 for the reports assumption). Otherwise complete roundtrip.
- **UJ protagonist naming:** All 8 UJs have named protagonists with userRole context. Lan appears in UJ-1, UJ-4, UJ-6, UJ-7, UJ-8 (HR Admin). Minh in UJ-2, UJ-5 (Employee). Hoa in UJ-3 (Employee). Supporting characters named: Tuan, Hung.
- **Required sections:** All expected sections present for internal-stakes B2B SaaS: Vision, Personas/JTBD, UJs, Glossary, Features with FRs, Non-Goals, MVP Scope, Success Metrics, NFRs, Constraints, Open Questions, Assumptions Index.
