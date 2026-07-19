export interface ChatRequestDTO {
  question: string;
}

export interface ChatResponseDTO {
  answer: string;
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}
