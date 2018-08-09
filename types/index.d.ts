interface Window {
    CallListener: CallListener;
}

interface CallListener {
    show: (color: string) => void;
}

declare var CallListener: CallListener;