import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';
import {RAY_GITHUB} from "@/constant";

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'github',
          title: <> <GithubOutlined /> Ray Github</>,
          href: RAY_GITHUB,
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
