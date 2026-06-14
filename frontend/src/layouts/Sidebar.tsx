import { Layout, Menu } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  DashboardOutlined,
  TeamOutlined,
  ClockCircleOutlined,
  FileTextOutlined,
  DollarOutlined,
  CheckSquareOutlined,
  BarChartOutlined,
  AuditOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';

const { Sider } = Layout;

interface SidebarProps {
  collapsed: boolean;
  onCollapse: (collapsed: boolean) => void;
}

const menuItems: MenuProps['items'] = [
  {
    key: 'main',
    label: 'CHÍNH',
    type: 'group',
    children: [
      { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
      { key: '/employees', icon: <TeamOutlined />, label: 'Nhân viên' },
      { key: '/attendance', icon: <ClockCircleOutlined />, label: 'Chấm công' },
      { key: '/leave', icon: <FileTextOutlined />, label: 'Nghỉ phép' },
      { key: '/approvals', icon: <CheckSquareOutlined />, label: 'Phê duyệt' },
      { key: '/payroll', icon: <DollarOutlined />, label: 'Tiền lương' },
    ],
  },
  {
    key: 'system',
    label: 'HỆ THỐNG',
    type: 'group',
    children: [
      { key: '/reports', icon: <BarChartOutlined />, label: 'Báo cáo' },
      { key: '/audit-log', icon: <AuditOutlined />, label: 'Audit Log' },
      { key: '/config', icon: <SettingOutlined />, label: 'Cấu hình' },
    ],
  },
];

export default function Sidebar({ collapsed, onCollapse }: SidebarProps) {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Sider
      collapsible
      collapsed={collapsed}
      onCollapse={onCollapse}
      width={260}
      collapsedWidth={72}
      theme="dark"
      style={{
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
        zIndex: 100,
        background: '#0F172A',
        backgroundImage: 'radial-gradient(ellipse at 20% 50%, rgba(22,119,255,0.12) 0%, transparent 70%)',
        overflow: 'auto',
      }}
    >
      <div
        style={{
          height: 72,
          display: 'flex',
          alignItems: 'center',
          padding: collapsed ? '0 16px' : '0 24px',
          gap: 12,
        }}
      >
        <div
          style={{
            width: 36,
            height: 36,
            borderRadius: 14,
            background: 'linear-gradient(135deg, #1677ff, #69b1ff)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 800,
            fontSize: 18,
            flexShrink: 0,
            boxShadow: '0 14px 30px rgba(22,119,255,0.30)',
          }}
        >
          H
        </div>
        {!collapsed && (
          <div>
            <div style={{ color: '#fff', fontWeight: 800, fontSize: 16, lineHeight: 1.2 }}>HRMS</div>
            <div style={{ color: 'rgba(255,255,255,0.45)', fontSize: 11, fontWeight: 500 }}>
              People Operations
            </div>
          </div>
        )}
      </div>

      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={({ key }) => navigate(key)}
        style={{
          background: 'transparent',
          borderRight: 'none',
        }}
      />
    </Sider>
  );
}
