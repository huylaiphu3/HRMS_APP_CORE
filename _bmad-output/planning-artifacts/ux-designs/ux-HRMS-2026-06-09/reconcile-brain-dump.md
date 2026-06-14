# Reconciliation: User Brain Dump vs UX Spines

**Source:** User brain dump (conversation input) | **Spines:** DESIGN.md + EXPERIENCE.md

## Coverage Check

| Input Item | DESIGN.md | EXPERIENCE.md | Status |
|-----------|-----------|---------------|--------|
| Balanced SaaS style (Base.vn + Jira + NG-ZORRO Pro) | Brand & Style section | Inspiration & Anti-patterns | ✅ Covered |
| Primary #1677FF | colors.primary | — | ✅ Covered |
| Success #52C41A, Warning #FAAD14, Danger #FF4D4F | colors.success/warning/danger | — | ✅ Covered |
| Sidebar trái layout | Layout & Spacing diagram | IA Sidebar Navigation table | ✅ Covered |
| Dashboard stat cards đơn giản, không biểu đồ phức tạp | Stat Card component | Flow 7 (Lan dashboard), Anti-pattern (complex charts rejected) | ✅ Covered |
| Data table: Filter mạnh, Search mạnh, Export Excel, Bulk Action, Column Setting | Data Table Toolbar component | Data Table component pattern (12 features) | ✅ Covered |
| Employee List có Search, Department Filter, Status Filter, Export, Import, Add ở trên | Data Table Toolbar | Data Table pattern | ✅ Covered |
| Workflow Step Builder form-based, không BPMN drag-drop | Step Builder Item component | Step Builder pattern + Flow 5 + Anti-pattern | ✅ Covered |
| Dark mode: có nhưng V1 không ưu tiên | Brand & Style (ConfigProvider darkAlgorithm) | Anti-pattern (dark mode opt-in) | ✅ Covered |
| Desktop First > Tablet > Mobile Basic | — | Responsive & Platform (4 breakpoints) | ✅ Covered |
| Topbar: Search, Notification Bell, Approval Badge, Avatar | — | Topbar section | ✅ Covered |
| Tốc độ thao tác cho HR Admin | Do's and Don'ts | Voice and Tone (ngắn gọn) + Interaction Primitives | ✅ Covered |
| Approval inbox rõ ràng | Approval Inbox Item component | Approval Inbox pattern + Flow 3 | ✅ Covered |

## Gaps Found

**Không có gap.** Mọi ý trong brain dump đã được capture trong một hoặc cả hai spines.

## Qualitative Ideas Not Explicitly Captured (Minor)

- "Dễ mở rộng module" — captured implicitly qua NG-ZORRO Pro structure + sidebar menu, nhưng không có section riêng về extensibility UX. Tuy nhiên đây là architectural concern hơn là UX concern. **No action needed.**
