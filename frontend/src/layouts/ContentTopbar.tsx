import { Input, Badge, Avatar, Dropdown } from 'antd';
import { SearchOutlined, BellOutlined, UserOutlined, LogoutOutlined } from '@ant-design/icons';
import type { MenuProps } from 'antd';

const avatarMenuItems: MenuProps['items'] = [
  { key: 'profile', icon: <UserOutlined />, label: 'Hồ sơ' },
  { type: 'divider' },
  { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất', danger: true },
];

export default function ContentTopbar() {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        padding: '16px 36px',
        gap: 16,
        background: '#F8FAFC',
      }}
    >
      <Input
        prefix={<SearchOutlined style={{ color: '#94A3B8' }} />}
        placeholder="Tìm kiếm nhân viên..."
        style={{
          width: 320,
          height: 44,
          borderRadius: 9999,
          marginRight: 'auto',
          boxShadow: '0 2px 10px rgba(15,23,42,0.04)',
        }}
      />

      <Badge count={0} size="small">
        <div
          style={{
            width: 40,
            height: 40,
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            transition: 'background 0.2s',
          }}
        >
          <BellOutlined style={{ fontSize: 18, color: '#64748B' }} />
        </div>
      </Badge>

      <Dropdown menu={{ items: avatarMenuItems }} trigger={['click']} placement="bottomRight">
        <Avatar
          style={{
            background: 'linear-gradient(135deg, #3b82f6, #60a5fa)',
            cursor: 'pointer',
            fontWeight: 800,
          }}
          size={40}
        >
          AD
        </Avatar>
      </Dropdown>
    </div>
  );
}
