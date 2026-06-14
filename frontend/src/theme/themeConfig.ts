import type { ThemeConfig } from 'antd';

const themeConfig: ThemeConfig = {
  token: {
    colorPrimary: '#1677FF',
    colorSuccess: '#10B981',
    colorWarning: '#F59E0B',
    colorError: '#EF4444',
    colorInfo: '#1677FF',
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
    borderRadius: 12,
    colorBgLayout: '#F8FAFC',
    colorBorder: '#E2E8F0',
    colorBorderSecondary: '#F1F5F9',
    controlHeight: 44,
  },
  components: {
    Card: {
      borderRadiusLG: 20,
      paddingLG: 22,
      boxShadowTertiary: '0 18px 50px rgba(15,23,42,0.06), 0 3px 10px rgba(15,23,42,0.04)',
    },
    Button: {
      controlHeight: 44,
      borderRadius: 12,
      fontWeight: 700,
    },
    Input: {
      controlHeight: 44,
      borderRadius: 12,
    },
    Select: {
      controlHeight: 44,
      borderRadius: 12,
    },
    DatePicker: {
      controlHeight: 44,
      borderRadius: 12,
    },
    Tag: {
      borderRadiusSM: 9999,
      fontSizeSM: 12,
    },
    Table: {
      headerBg: '#F8FAFF',
      headerColor: '#94A3B8',
      rowHoverBg: '#FBFDFF',
      cellPaddingBlock: 16,
      cellPaddingInline: 24,
    },
    Menu: {
      darkItemBg: '#0F172A',
      darkSubMenuItemBg: '#0F172A',
      darkItemSelectedBg: 'rgba(22,119,255,0.12)',
      darkItemHoverBg: 'rgba(255,255,255,0.06)',
      itemHeight: 42,
      itemBorderRadius: 13,
    },
  },
};

export default themeConfig;
