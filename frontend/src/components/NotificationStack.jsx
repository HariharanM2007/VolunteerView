import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { removeNotification } from '../store/slices/notificationSlice';

const NotificationStack = () => {
  const dispatch = useDispatch();
  const notifications = useSelector((state) => state.notifications?.list || []);

  if (!notifications.length) return null;

  return (
    <div className="notification-stack">
      {notifications.map((note) => (
        <div
          key={note.id}
          className={`notification-item ${note.type}`}
          onClick={() => dispatch(removeNotification(note.id))}
        >
          {note.message}
        </div>
      ))}
    </div>
  );
};

export default NotificationStack;
