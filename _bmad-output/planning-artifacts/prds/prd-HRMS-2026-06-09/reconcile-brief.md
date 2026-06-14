# Reconciliation: Brief vs PRD — Gaps Found

**Source brief:** `brief-HRMS-2026-06-08/brief.md` | **PRD:** `prd-HRMS-2026-06-09/prd.md`

7 gaps identified where the brief's intent was lost or weakened in the PRD.

---

## Gaps

1. **Open-source identity and self-hosted positioning diluted**
   - **Brief:** Core differentiator is "ma nguon mo, tu trien khai" — businesses own their data, no monthly SaaS fee, just need one server. Repeated in summary, differentiators, and vision sections.
   - **PRD:** Reframed as "SaaS-ready" with multi-tenant architecture. The open-source, self-hosted, zero-cost positioning — the primary reason a company would choose this over Base.vn/AMIS — is no longer a stated differentiator. PRD Section 1 competes on price ("120-200K VND/user/month") but never commits to being free/open-source.
   - **Severity:** Critical

2. **Data import from Excel not addressed**
   - **Brief:** Assumption explicitly states "Du lieu nhan su hien tai cua doanh nghiep (neu co) se duoc import thu cong hoac qua file Excel" — recognizing that migrating from Excel is a real onboarding need.
   - **PRD:** No FR covers data import. No mention in assumptions, open questions, or out-of-scope. The feature simply disappeared.
   - **Severity:** Medium

3. **"Customization flexibility" differentiator dropped**
   - **Brief:** Key differentiator: "Tuy chinh linh hoat — Doanh nghiep dieu chinh quy trinh theo dac thu rieng — dieu khong the voi SaaS dong goi." This is a product promise.
   - **PRD:** The dynamic workflow engine partially addresses this, but the broader customization promise (adapting processes to each company's specifics) is not captured as a design principle or NFR. No extensibility or plugin architecture is mentioned.
   - **Severity:** Low

4. **Community and ecosystem vision absent**
   - **Brief:** Vision section: "Xay dung cong dong contributor va ecosystem plugin de mo rong tinh nang." Positions HRMS alongside ERPNext/Odoo.
   - **PRD:** Vision (Section 1) mentions nothing about community, contributors, or plugin ecosystem. The open-source community aspiration — a core part of the product's long-term identity — is entirely missing.
   - **Severity:** Low

5. **Leave approval flow changed without noting deviation**
   - **Brief:** Leave approval is "Employee -> Manager -> HR" (two-step after employee).
   - **PRD:** Default workflow is "Employee -> Direct Manager" (one-step). The brief's HR-as-final-approver default was silently changed. The dynamic workflow engine allows configuring the brief's flow, but the default shipped is different from what the brief specified.
   - **Severity:** Medium

6. **Success criterion "half a day for payroll" weakened to "4 hours"**
   - **Brief:** "HR Admin chay duoc bang luong thang trong nua ngay lam viec (thay vi 2-3 ngay thu cong)." — "half a working day" = ~4 hours, so numerically similar.
   - **PRD:** SM-1 says "le 4 gio." The spirit is preserved but the brief's phrasing ("nua ngay") is more user-friendly and sets a relative expectation. Minor, but the PRD quietly tightened it to a hard number.
   - **Severity:** Low

7. **Risk of SMBs lacking IT staff not addressed**
   - **Brief:** Risk: "Doanh nghiep nho co the thieu nhan luc IT de tu trien khai va bao tri he thong." This is a real adoption risk.
   - **PRD:** Section 8 (NFRs) mentions Docker Compose deployment and documentation for basic Docker knowledge, but never acknowledges this risk or proposes mitigation (e.g., one-click deploy script, managed hosting option, or simplified setup wizard). The brief's risk disappeared without being accepted, mitigated, or deferred.
   - **Severity:** Medium
