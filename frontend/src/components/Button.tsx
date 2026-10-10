interface ButtonProps {
  label: string;
  onClick: () => void;
  className?: string;
  isActive?: boolean;
}

function Button({ label, onClick, className = '', isActive = false }: ButtonProps) {
  return (
    <button className={`calc-btn ${className} ${isActive ? 'btn-active' : ''}`} onClick={onClick}>
      {label}
    </button>
  );
}

export default Button;
